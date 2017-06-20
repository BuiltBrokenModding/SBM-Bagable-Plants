package com.builtbroken.bagableplants.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
public class InteractionHandler
{
    /**
     * Adds information to the bag item about the contained plant
     *
     * @param stack  - the bag itself, check NBT for data
     * @param player - the player holding (or contains in inventory) the bag
     * @param list   - place to add information to
     * @param b      - unknown
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, ItemStack blockStack, NBTTagCompound blockStackExtra, EntityPlayer player, List list, boolean b)
    {
        if (blockStack != null)
        {
            list.add(StatCollector.translateToLocal(blockStack.getUnlocalizedName() + ".name"));
        }
    }

    /**
     * Called to pickup the block and encode the data into the the bag stack
     *
     * @param world
     * @param bag   - stack of empty bags, reduce stack by 1
     * @return encoded bag
     */
    public ItemStack pickupBlock(World world, BlockPos pos, ItemStack bag)
    {
        return bag;
    }

    /**
     * Can this handler pickup the block at the location
     *
     * @param world
     * @return
     */
    public boolean canPickupBlock(World world, BlockPos pos)
    {
        return true;
    }

    /**
     * Can the block be placed at the location from the block stack
     *
     * @param world
     * @param blockStack
     * @param blockStackExtra
     * @return
     */
    public boolean canPlaceBlock(World world, BlockPos pos, ItemStack blockStack, NBTTagCompound blockStackExtra)
    {
        Block placement = Block.getBlockFromItem(blockStack.getItem());
        BlockPos posDown = pos.down();
        if (world.isAirBlock(posDown))
        {
            return placement.canPlaceBlockAt(world, pos);
        }
        return false;
    }

    /**
     * Called to place the block
     *
     * @param world
     * @param blockStack
     * @return true if the block was placed
     */
    public boolean placeBlock(World world, BlockPos pos, ItemStack blockStack, NBTTagCompound extraTag)
    {
        return false;
    }
}
