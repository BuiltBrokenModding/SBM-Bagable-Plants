package com.builtbroken.bagableplants;

import com.builtbroken.bagableplants.handler.InteractionHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
public class ItemBag extends Item
{
    @SideOnly(Side.CLIENT)
    public IIcon filledIcon;

    public ItemBag()
    {
        setUnlocalizedName("bagableplants:bag");
        setTextureName("bagableplants:bag");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        ItemStack blockStack = getBlockStack(stack);
        if (blockStack != null)
        {
            list.add(StatCollector.translateToLocal(blockStack.getUnlocalizedName() + ".name"));
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xx, float yy, float zz)
    {
        ItemStack blockStack = getBlockStack(stack);
        if (blockStack == null)
        {
            Block block = world.getBlock(x, y, z);
            if (block != Blocks.air)
            {
                InteractionHandler handler = BagablePlants.blockNameToHandler.get(block);
                if (handler != null && handler.canPickupBlock(world, x, y, z))
                {
                    ItemStack copy = stack.copy();
                    ItemStack result = handler.pickupBlock(world, x, y, z, copy);
                    if (result != null)
                    {
                        if (!world.isRemote)
                        {
                            if (copy == null || copy.stackSize <= 0)
                            {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, result);
                            }
                            else if (!player.inventory.addItemStackToInventory(result))
                            {
                                player.dropPlayerItemWithRandomChoice(result, false);
                            }
                        }
                        return true;
                    }
                }
            }
        }
        else
        {
            Block block = Block.getBlockFromItem(blockStack.getItem());
            if (block != null && block != Blocks.air)
            {
                InteractionHandler handler = BagablePlants.blockNameToHandler.get(block);
                if (handler != null && handler.canPlaceBlock(world, x, y, z, blockStack))
                {
                    if (handler.placeBlock(world, x, y, z, blockStack))
                    {
                        if (!world.isRemote && !player.capabilities.isCreativeMode)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(this));
                        }
                        return true;
                    }
                }
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
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(this.getIconString());
        this.filledIcon = reg.registerIcon(this.getIconString() + ".filled");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta == 1)
        {
            return this.filledIcon;
        }
        return this.itemIcon;
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
