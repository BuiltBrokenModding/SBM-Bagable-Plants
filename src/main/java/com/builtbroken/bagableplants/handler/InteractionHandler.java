package com.builtbroken.bagableplants.handler;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
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
     * @param list   - place to add information to
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(World world, ItemStack stack, ItemStack blockStack, NBTTagCompound blockStackExtra, List<String> list, ITooltipFlag flagIn)
    {
        if (blockStack != null)
        {
            list.add(I18n.translateToLocal(blockStack.getUnlocalizedName() + ".name"));
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
    public boolean canPlaceBlock(World world, BlockPos pos, Block placement, ItemStack blockStack, NBTTagCompound blockStackExtra)
    {
        BlockPos posDown = pos.down();
        if (!world.isAirBlock(posDown))
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
