package com.builtbroken.baggableplants;

import java.util.HashMap;

import com.builtbroken.baggableplants.handler.InteractionHandler;
import com.builtbroken.baggableplants.handler.VanillaHandler;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
@Mod(BaggablePlants.MODID)
@Mod.EventBusSubscriber(bus=Bus.MOD)
public class BaggablePlants
{
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;
    public static final String MODID = "baggableplants";

    @ObjectHolder(MODID + ":bag")
    public static Item itemBag;
    @ObjectHolder(MODID + ":filled_bag")
    public static Item filledBag;

    /** Map of objects to handlers in minecraft */
    public static final HashMap<Object, InteractionHandler> blockNameToHandler = new HashMap<>();
    /** Map of items to blocks, corrects for items that are not ItemBlocks (ex. reeds) */
    public static final HashMap<Item, Block> itemToBlockMap = new HashMap<>();

    public BaggablePlants()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
    }

    /**
     * Called to register a block with a handler
     *
     * @param block   - block instance
     * @param handler - handler instance
     * @return true if registered
     */
    public static boolean register(Block block, InteractionHandler handler)
    {
        if (block != null && handler != null)
        {
            blockNameToHandler.put(block, handler);
            return true;
        }
        return false;
    }

    public static boolean register(Item item, InteractionHandler handler)
    {
        if (item != null && handler != null)
        {
            blockNameToHandler.put(item, handler);
            return true;
        }
        return false;
    }

    /**
     * Called to register a block with a handler
     *
     * @param name    - registered name of the block
     * @param handler - handler
     * @return true if registered
     */
    public static boolean register(ResourceLocation name, InteractionHandler handler)
    {
        if (name != null && handler != null)
        {
            Block block = ForgeRegistries.BLOCKS.getValue(name);
            if (block != null)
            {
                blockNameToHandler.put(block, handler);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBag(false));
        event.getRegistry().register(new ItemBag(true));
    }

    public void postInit(InterModProcessEvent event)
    {
        VanillaHandler.register();
    }

    public static Block getBlockFromItem(Item item)
    {
        if (item instanceof ItemBlock)
        {
            return ((ItemBlock) item).getBlock();
        }
        return itemToBlockMap.get(item);
    }
}
