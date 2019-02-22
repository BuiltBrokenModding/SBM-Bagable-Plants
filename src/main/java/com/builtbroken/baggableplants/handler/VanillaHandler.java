package com.builtbroken.baggableplants.handler;

import java.util.List;

import com.builtbroken.baggableplants.BaggablePlants;
import com.builtbroken.baggableplants.ItemBag;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
public class VanillaHandler extends InteractionHandler
{
    public static void register()
    {
        VanillaHandler handler = new VanillaHandler();
        BaggablePlants.register(Blocks.SUGAR_CANE, handler);
        BaggablePlants.register(Blocks.SUGAR_CANE.asItem(), handler);
        BaggablePlants.itemToBlockMap.put(Blocks.SUGAR_CANE.asItem(), Blocks.SUGAR_CANE);
        BaggablePlants.register(Blocks.CACTUS, handler);
    }

    @Override
    public ItemStack pickupBlock(World world, BlockPos pos, ItemStack bagStack)
    {
        Block block = world.getBlockState(pos).getBlock();
        ItemStack stack = new ItemStack(BaggablePlants.filledBag);
        stack.setCount(1);
        if (block == Blocks.SUGAR_CANE)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.putInt("count", breakAndGetCount(world, pos, Blocks.SUGAR_CANE));
            ItemBag.encodeBlock(stack, new ItemStack(Blocks.SUGAR_CANE.asItem()), nbt);
            bagStack.setCount(bagStack.getCount() - 1);
            return stack;
        }
        else if (block == Blocks.CACTUS)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.putInt("count", breakAndGetCount(world, pos, Blocks.CACTUS));
            ItemBag.encodeBlock(stack, new ItemStack(Blocks.CACTUS), nbt);
            bagStack.setCount(bagStack.getCount() - 1);
            return stack;
        }
        return bagStack;
    }

    private int breakAndGetCount(World world, final BlockPos start, Block blockToMatch)
    {
        int count = 1; //already count top block
        BlockPos pos = start;

        //find top
        while(world.getBlockState(pos.up()).getBlock() == blockToMatch)
        {
            pos = pos.up();
        }

        //delete top block
        world.removeBlock(pos);

        //count and delete
        while(world.getBlockState(pos.down()).getBlock() == blockToMatch)
        {
            count++;
            pos = pos.down();
            world.removeBlock(pos);
        }

        return count;
    }

    @Override
    public boolean placeBlock(World world, BlockPos start, ItemStack blockStack, NBTTagCompound extra)
    {
        BlockPos pos = start;
        int count = extra.getInt("count");
        if (blockStack.getItem() == Blocks.SUGAR_CANE.asItem())
        {
            while (count > 0)
            {
                world.setBlockState(pos, Blocks.SUGAR_CANE.getDefaultState());
                pos = pos.up();
                count--;
            }
            return true;
        }
        else if (Block.getBlockFromItem(blockStack.getItem()) == Blocks.CACTUS)
        {
            while (count > 0)
            {
                world.setBlockState(pos, Blocks.CACTUS.getDefaultState());
                pos = pos.up();
                count--;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceBlock(World world, BlockPos pos, Block placement, ItemStack blockStack, NBTTagCompound blockStackExtra)
    {
        if (super.canPlaceBlock(world, pos, placement, blockStack, blockStackExtra))
        {
            Block block = Block.getBlockFromItem(blockStack.getItem());

            if (block == Blocks.CACTUS || blockStack.getItem() == Blocks.SUGAR_CANE.asItem())
            {
                int count = blockStackExtra.getInt("count");
                while (count > 1)
                {
                    block = world.getBlockState(pos.up()).getBlock();
                    if (!block.isReplaceable(world.getBlockState(pos), new BlockItemUseContext(world, null, blockStack, pos, EnumFacing.UP, 0, 0, 0)))
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
    @OnlyIn(Dist.CLIENT)
    public void addInformation(World world, ItemStack stack, ItemStack blockStack, NBTTagCompound blockStackExtra, List<ITextComponent> list, ITooltipFlag flagIn)
    {
        super.addInformation(world, stack, blockStack, blockStackExtra, list, flagIn);
        Block block = Block.getBlockFromItem(blockStack.getItem());
        if (block == Blocks.CACTUS || blockStack.getItem() == Blocks.SUGAR_CANE.asItem())
        {
            int count = blockStackExtra.getInt("count");
            list.add(new TextComponentTranslation(BaggablePlants.itemBag.getTranslationKey() + ".height", count));
        }
    }
}
