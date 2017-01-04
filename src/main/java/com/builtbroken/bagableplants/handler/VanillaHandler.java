package com.builtbroken.bagableplants.handler;

import com.builtbroken.bagableplants.BagablePlants;
import com.builtbroken.bagableplants.ItemBag;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
    public ItemStack pickupBlock(World world, int x, int y, int z, ItemStack bagStack)
    {
        Block block = world.getBlock(x, y, z);
        ItemStack stack = bagStack.copy();
        stack.stackSize = 1;
        if (block == Blocks.reeds)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("count", breakAndGetCount(world, x, y, z, Blocks.reeds));
            ItemBag.encodeBlock(stack, new ItemStack(Items.reeds), nbt);
            bagStack.stackSize--;
            return stack;
        }
        else if (block == Blocks.cactus)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("count", breakAndGetCount(world, x, y, z, Blocks.cactus));
            ItemBag.encodeBlock(stack, new ItemStack(Blocks.cactus), nbt);
            bagStack.stackSize--;
            return stack;
        }
        return bagStack;
    }

    private int breakAndGetCount(World world, int x, int y, int z, Block blockToMatch)
    {
        int count = 0;
        Block block;
        while (true)
        {
            y--;
            block = world.getBlock(x, y, z);
            if (block != blockToMatch)
            {
                y++;
                block = world.getBlock(x, y, z);
                break;
            }
        }
        while (block == blockToMatch)
        {
            world.setBlockToAir(x, y, z);
            count++;
            y++;
            block = world.getBlock(x, y, z);
        }
        return count;
    }

    @Override
    public boolean placeBlock(World world, int x, int y, int z, ItemStack blockStack, NBTTagCompound extra)
    {
        int count = extra.getInteger("count");
        if (blockStack.getItem() == Items.reeds)
        {
            while (count > 0)
            {
                world.setBlock(x, y, z, Blocks.reeds);
                y++;
                count--;
            }
            return true;
        }
        else if(Block.getBlockFromItem(blockStack.getItem()) == Blocks.cactus)
        {
            while (count > 0)
            {
                world.setBlock(x, y, z, Blocks.cactus);
                y++;
                count--;
            }
            return true;
        }
        return false;
    }
}
