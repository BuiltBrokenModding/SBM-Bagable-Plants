package com.builtbroken.bagableplants;

import com.builtbroken.bagableplants.handler.InteractionHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
public class ItemBag extends Item
{
    public ItemBag()
    {
        setUnlocalizedName("bagableplants:bag");
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        ItemStack blockStack = getBlockStack(stack);
        if (blockStack != null)
        {
            InteractionHandler handler = null;
            Block block = Block.getBlockFromItem(blockStack.getItem());
            if (block != null && block != Blocks.air)
            {
                handler = BagablePlants.blockNameToHandler.get(block);
            }
            if (handler == null)
            {
                handler = BagablePlants.blockNameToHandler.get(blockStack.getItem());
            }
            if (handler != null)
            {
                handler.addInformation(stack, blockStack, getBlockStackExtra(stack), player, list, b);
            }
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos clickPos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack blockStack = getBlockStack(stack);
        if (blockStack == null)
        {
            Block block = world.getBlockState(clickPos).getBlock();
            if (block != Blocks.air)
            {
                InteractionHandler handler = BagablePlants.blockNameToHandler.get(block);
                if (handler != null && handler.canPickupBlock(world, clickPos))
                {
                    ItemStack copy = stack.copy();
                    ItemStack result = handler.pickupBlock(world, clickPos, copy);
                    if (result != null)
                    {
                        if (!world.isRemote)
                        {
                            if (copy == null || copy.stackSize <= 0)
                            {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, result);
                            }
                            else
                            {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, copy);
                                if (!player.inventory.addItemStackToInventory(result))
                                {
                                    player.dropPlayerItemWithRandomChoice(result, false);
                                }
                            }
                            player.inventoryContainer.detectAndSendChanges();
                        }
                        return true;
                    }
                }
            }
        }
        else
        {
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
            Block block = BagablePlants.getBlockFromItem(blockStack.getItem());
            if (block != null && block != Blocks.air)
            {
                handler = BagablePlants.blockNameToHandler.get(block);
            }
            if (handler == null)
            {
                handler = BagablePlants.blockNameToHandler.get(blockStack.getItem());
            }
            if (handler != null)
            {
                if (handler.canPlaceBlock(world, pos, block, blockStack, getBlockStackExtra(stack)))
                {
                    if (handler.placeBlock(world, pos, blockStack, getBlockStackExtra(stack)))
                    {
                        if (!world.isRemote && !player.capabilities.isCreativeMode)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(this));
                        }
                    }
                }
                else if (!world.isRemote)
                {
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".cantPlace.name")));
                }
                return true;
            }

        }
        return false;
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
        if (bag.getTagCompound() == null)
        {
            bag.setTagCompound(new NBTTagCompound());
        }
        bag.getTagCompound().setTag("data", block.writeToNBT(new NBTTagCompound()));
        bag.getTagCompound().setTag("extra", extra);
        bag.setItemDamage(1); //Meta is used to set the icon
    }

    /**
     * Gets the ItemStack that represents the stored block
     *
     * @param bag
     * @return
     */
    public static ItemStack getBlockStack(ItemStack bag)
    {
        if (bag.getTagCompound() == null || !bag.getTagCompound().hasKey("data"))
        {
            return null;
        }
        return ItemStack.loadItemStackFromNBT(bag.getTagCompound().getCompoundTag("data"));
    }

    /**
     * Gets the extra data that goes with the block stored
     *
     * @param bag
     * @return
     */
    public static NBTTagCompound getBlockStackExtra(ItemStack bag)
    {
        if (bag.getTagCompound() == null || !bag.getTagCompound().hasKey("data"))
        {
            return null;
        }
        return bag.getTagCompound().getCompoundTag("extra");
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        if (stack.getTagCompound() != null)
        {
            return 1;
        }
        return 64;
    }
}
