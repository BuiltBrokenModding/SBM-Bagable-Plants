package com.builtbroken.bagableplants.handler;

import com.builtbroken.bagableplants.BagablePlants;
import com.builtbroken.bagableplants.ItemBag;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
public class VanillaHandler extends InteractionHandler
{
    public static void register()
    {
        VanillaHandler handler = new VanillaHandler();
        BagablePlants.register(Blocks.reeds, handler);
        BagablePlants.register(Items.reeds, handler);
        BagablePlants.register(Blocks.cactus, handler);
    }

    @Override
    public ItemStack pickupBlock(World world, BlockPos pos, ItemStack bagStack)
    {
        Block block = world.getBlockState(pos).getBlock();
        ItemStack stack = bagStack.copy();
        stack.stackSize = 1;
        if (block == Blocks.reeds)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("count", breakAndGetCount(world, pos, Blocks.reeds));
            ItemBag.encodeBlock(stack, new ItemStack(Items.reeds), nbt);
            bagStack.stackSize--;
            return stack;
        }
        else if (block == Blocks.cactus)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("count", breakAndGetCount(world, pos, Blocks.cactus));
            ItemBag.encodeBlock(stack, new ItemStack(Blocks.cactus), nbt);
            bagStack.stackSize--;
            return stack;
        }
        return bagStack;
    }

    private int breakAndGetCount(World world, final BlockPos start, Block blockToMatch)
    {
        int count = 0;
        Block block;
        BlockPos pos = start;

        //Find bottom
        while (true)
        {
            pos = start.down();
            block = world.getBlockState(pos).getBlock();
            if (block != blockToMatch)
            {
                pos = start.up();
                block = world.getBlockState(pos).getBlock();
                break;
            }
        }

        //Delete loop
        while (block == blockToMatch)
        {
            world.setBlockToAir(pos);
            count++;
            pos = start.up();
            block = world.getBlockState(pos).getBlock();
        }
        return count;
    }

    @Override
    public boolean placeBlock(World world, BlockPos start, ItemStack blockStack, NBTTagCompound extra)
    {
        BlockPos pos = start;
        int count = extra.getInteger("count");
        if (blockStack.getItem() == Items.reeds)
        {
            while (count > 0)
            {
                world.setBlockState(pos, Blocks.reeds.getDefaultState());
                pos = start.up();
                count--;
            }
            return true;
        }
        else if (Block.getBlockFromItem(blockStack.getItem()) == Blocks.cactus)
        {
            while (count > 0)
            {
                world.setBlockState(pos, Blocks.cactus.getDefaultState());
                pos = start.up();
                count--;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceBlock(World world, BlockPos pos, ItemStack blockStack, NBTTagCompound blockStackExtra)
    {
        if (super.canPlaceBlock(world, pos, blockStack, blockStackExtra))
        {
            Block block = Block.getBlockFromItem(blockStack.getItem());

            if (block == Blocks.cactus || blockStack.getItem() == Items.reeds)
            {
                int count = blockStackExtra.getInteger("count");
                while (count > 1)
                {
                    block = world.getBlockState(pos.up()).getBlock();
                    if (!block.isReplaceable(world, pos))
                    {
                        return false;
                    }
                    count--;
                }
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, ItemStack blockStack, NBTTagCompound blockStackExtra, EntityPlayer player, List list, boolean b)
    {
        super.addInformation(stack, blockStack, blockStackExtra, player, list, b);
        Block block = Block.getBlockFromItem(blockStack.getItem());
        if (block == Blocks.cactus || blockStack.getItem() == Items.reeds)
        {
            int count = blockStackExtra.getInteger("count");
            list.add(StatCollector.translateToLocal("item.bagableplants:bag.height.name").replace("%1", "" + count));
        }
    }
}
