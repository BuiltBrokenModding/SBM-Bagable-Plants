package com.builtbroken.baggableplants;

import java.util.List;

import javax.annotation.Nullable;

import com.builtbroken.baggableplants.handler.InteractionHandler;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
public class ItemBag extends Item
{
    public final boolean filled;

    public ItemBag(boolean filled)
    {
        super(new Item.Properties().group(ItemGroup.TOOLS));
        this.filled = filled;
        setRegistryName(BaggablePlants.MODID + (filled ? ":filled_bag" : ":bag"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn)
    {
        ItemStack blockStack = getBlockStack(stack);
        if (blockStack != null)
        {
            InteractionHandler handler = null;
            Block block = Block.getBlockFromItem(blockStack.getItem());
            if (block != null && block != Blocks.AIR)
            {
                handler = BaggablePlants.blockNameToHandler.get(block);
            }
            if (handler == null)
            {
                handler = BaggablePlants.blockNameToHandler.get(blockStack.getItem());
            }
            if (handler != null)
            {
                handler.addInformation(world, stack, blockStack, getBlockStackExtra(stack), list, flagIn);
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext ctx)
    {
        EntityPlayer player = ctx.getPlayer();

        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        if(stack.getItem() != BaggablePlants.filledBag && stack.getItem() != BaggablePlants.itemBag)
        {
            System.out.println(stack.getItem() == BaggablePlants.itemBag);
            System.out.println(stack.getItem() == BaggablePlants.filledBag);
            return EnumActionResult.PASS;
        }

        World world = ctx.getWorld();
        BlockPos clickPos = ctx.getPos();
        EnumFacing side = ctx.getFace();

        if (!filled)
        {
            Block block = world.getBlockState(clickPos).getBlock();
            if (block != Blocks.AIR)
            {
                InteractionHandler handler = BaggablePlants.blockNameToHandler.get(block);
                if (handler != null && handler.canPickupBlock(world, clickPos))
                {
                    ItemStack copy = stack.copy();
                    ItemStack result = handler.pickupBlock(world, clickPos, copy);
                    if (result != null)
                    {
                        if (!world.isRemote)
                        {
                            if (copy == null || copy.getCount() <= 0)
                            {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, result);
                            }
                            else
                            {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, copy);
                                if (!player.inventory.addItemStackToInventory(result))
                                {
                                    player.dropItem(result, false);
                                }
                            }
                            player.inventoryContainer.detectAndSendChanges();
                        }
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        else
        {
            ItemStack blockStack = getBlockStack(stack);
            BlockPos pos = clickPos;
            if (side.ordinal() == 0)
            {
                pos = pos.down();
            }

            if (side.ordinal() == 1)
            {
                pos = pos.up();
            }

            if (side.ordinal() == 2)
            {
                pos = pos.north();
            }

            if (side.ordinal() == 3)
            {
                pos = pos.south();
            }

            if (side.ordinal() == 4)
            {
                pos = pos.west();
            }

            if (side.ordinal() == 5)
            {
                pos = pos.east();
            }
            InteractionHandler handler = null;
            Block block = BaggablePlants.getBlockFromItem(blockStack.getItem());
            if (block != null && block != Blocks.AIR)
            {
                handler = BaggablePlants.blockNameToHandler.get(block);
            }
            if (handler == null)
            {
                handler = BaggablePlants.blockNameToHandler.get(blockStack.getItem());
            }
            if (handler != null)
            {
                if (handler.canPlaceBlock(world, pos, block, blockStack, getBlockStackExtra(stack)))
                {
                    if (handler.placeBlock(world, pos, blockStack, getBlockStackExtra(stack)))
                    {
                        if (!world.isRemote && !player.isCreative())
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(BaggablePlants.itemBag));
                        }
                    }
                }
                else if (!world.isRemote)
                {
                    player.sendMessage(new TextComponentTranslation(BaggablePlants.itemBag.getTranslationKey() + ".cantPlace"));
                }
                return EnumActionResult.SUCCESS;
            }

        }
        return EnumActionResult.FAIL;
    }

    /**
     * Encodes the data for the bag to store
     *
     * @param bag
     * @param block
     * @param extra
     */
    public static void encodeBlock(ItemStack bag, ItemStack block, NBTTagCompound extra)
    {
        if (bag.getTag() == null)
        {
            bag.setTag(new NBTTagCompound());
        }
        bag.getTag().put("data", block.write(new NBTTagCompound()));
        bag.getTag().put("extra", extra);
    }

    /**
     * Gets the ItemStack that represents the stored block
     *
     * @param bag
     * @return
     */
    public static ItemStack getBlockStack(ItemStack bag)
    {
        if (bag.getTag() == null || !bag.getTag().contains("data"))
        {
            return null;
        }
        return ItemStack.read(bag.getTag().getCompound("data"));
    }

    /**
     * Gets the extra data that goes with the block stored
     *
     * @param bag
     * @return
     */
    public static NBTTagCompound getBlockStackExtra(ItemStack bag)
    {
        if (bag.getTag() == null || !bag.getTag().contains("data"))
        {
            return null;
        }
        return bag.getTag().getCompound("extra");
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        if (stack.getTag() != null)
        {
            return 1;
        }
        return 64;
    }
}
