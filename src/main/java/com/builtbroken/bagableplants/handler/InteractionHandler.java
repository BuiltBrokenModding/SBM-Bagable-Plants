package com.builtbroken.bagableplants.handler;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
public class InteractionHandler
{
    /**
     * Called to pickup the block and encode the data into the the bag stack
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param bag   - stack of empty bags, reduce stack by 1
     * @return encoded bag
     */
    public ItemStack pickupBlock(World world, int x, int y, int z, ItemStack bag)
    {
        return bag;
    }

    /**
     * Can this handler pickup the block at the location
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean canPickupBlock(World world, int x, int y, int z)
    {
        return true;
    }

    /**
     * Can the block be placed at the location from the block stack
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param blockStack
     * @return
     */
    public boolean canPlaceBlock(World world, int x, int y, int z, ItemStack blockStack)
    {
        Block placement = Block.getBlockFromItem(blockStack.getItem());
        Block block = world.getBlock(x, y - 1, z);
        if (block != Blocks.air)
        {
            return placement.canPlaceBlockAt(world, x, y, z);
        }
        return false;
    }

    /**
     * Called to place the block
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param blockStack
     * @return true if the block was placed
     */
    public boolean placeBlock(World world, int x, int y, int z, ItemStack blockStack, NBTTagCompound extraTag)
    {
        return false;
    }
}
