package com.builtbroken.bagableplants.handler.atm;

import com.builtbroken.bagableplants.BagablePlants;
import com.builtbroken.bagableplants.ItemBag;
import com.builtbroken.bagableplants.handler.InteractionHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

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
            ItemStack reBag = bag.copy();
            reBag.stackSize = 1;
            ItemBag.encodeBlock(reBag, new ItemStack(block, 1, world.getBlockMetadata(x, y, z)), new NBTTagCompound());
            if (!world.isRemote)
            {
                world.setBlockToAir(x, y, z);
                bag.stackSize--;            }

            return reBag;
        }
        else if (block == cassisTree)
        {
            NBTTagCompound extra = new NBTTagCompound();
            if (world.getBlock(x, y + 1, z) == cassisTree)
            {
                new ItemStack(world.getBlock(x, y + 1, z), 1, world.getBlockMetadata(x, y + 1, z)).writeToNBT(extra);
            }

            ItemStack reBag = bag.copy();
            reBag.stackSize = 1;
            ItemBag.encodeBlock(reBag, new ItemStack(block, 1, world.getBlockMetadata(x, y, z)), extra);

            if (!world.isRemote)
            {
                world.setBlockToAir(x, y, z);
                world.setBlockToAir(x, y + 1, z);
                bag.stackSize--;
            }

            return reBag;
        }
        return bag;
    }

    @Override
    public boolean canPlaceBlock(World world, int x, int y, int z, ItemStack blockStack, NBTTagCompound blockStackExtra)
    {
        if (super.canPlaceBlock(world, x, y, z, blockStack, blockStackExtra))
        {
            Block block = Block.getBlockFromItem(blockStack.getItem());
            if (block == cassisTree)
            {
                return world.getBlock(x, y + 1, z).isReplaceable(world, x, y + 1, z);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean placeBlock(World world, int x, int y, int z, ItemStack blockStack, NBTTagCompound extra)
    {
        Block block = Block.getBlockFromItem(blockStack.getItem());
        if (block == teaTree)
        {
            world.setBlock(x, y, z, teaTree, blockStack.getItemDamage(), 3);
            return true;
        }
        else if (block == cassisTree)
        {
            world.setBlock(x, y, z, cassisTree, blockStack.getItemDamage(), 3);
            if (extra != null && !extra.hasNoTags())
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(extra);
                if(stack != null)
                {
                    block = Block.getBlockFromItem(stack.getItem());
                    if (block == cassisTree)
                    {
                        world.setBlock(x, y + 1, z, cassisTree, stack.getItemDamage(), 3);
                    }
                }
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
        if (block == cassisTree)
        {
            list.add(StatCollector.translateToLocal("item.bagableplants:bag.height.name").replace("%1", "" + 2));
        }
    }
}
