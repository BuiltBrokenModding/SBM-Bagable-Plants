package com.builtbroken.bagableplants.handler.atm;

import com.builtbroken.bagableplants.BagablePlants;
import com.builtbroken.bagableplants.ItemBag;
import com.builtbroken.bagableplants.handler.InteractionHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
public class AppleTeaMilkHandler extends InteractionHandler
{
    public static final String teaTreeName = "DCsAppleMilk:defeatedcrow.teaTree";
    public static final String cassisTreeName = "DCsAppleMilk:defeatedcrow.cassisTree";

    private static Block teaTree;
    private static Block cassisTree;

    public static void register()
    {
        teaTree = Block.getBlockFromName(teaTreeName);
        cassisTree = Block.getBlockFromName(cassisTreeName);

        if (teaTree != null)
        {
            BagablePlants.register(teaTree, new AppleTeaMilkHandler());
        }

        if (cassisTree != null)
        {
            BagablePlants.register(cassisTree, new AppleTeaMilkHandler());
        }
    }

    @Override
    public ItemStack pickupBlock(World world, int x, int y, int z, ItemStack bag)
    {
        Block block = world.getBlock(x, y, z);
        if (block == teaTree)
        {
            ItemBag.encodeBlock(bag, new ItemStack(block, 1, world.getBlockMetadata(x, y, z)), new NBTTagCompound());
            if (!world.isRemote)
            {
                world.setBlockToAir(x, y + 1, z);
            }
        }
        else if (block == cassisTree)
        {
            NBTTagCompound extra = new NBTTagCompound();
            if (world.getBlock(x, y + 1, z) == cassisTree)
            {
                new ItemStack(world.getBlock(x, y + 1, z), 1, world.getBlockMetadata(x, y + 1, z)).writeToNBT(extra);
                if (!world.isRemote)
                {
                    world.setBlockToAir(x, y, z);
                }
                if (!world.isRemote)
                {
                    world.setBlockToAir(x, y + 1, z);
                }
            }
            ItemBag.encodeBlock(bag, new ItemStack(block, 1, world.getBlockMetadata(x, y, z)), extra);
        }
        return bag;
    }

    @Override
    public boolean placeBlock(World world, int x, int y, int z, ItemStack blockStack)
    {
        return false;
    }
}
