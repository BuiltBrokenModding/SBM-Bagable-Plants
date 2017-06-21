package com.builtbroken.bagableplants;

import com.builtbroken.bagableplants.handler.InteractionHandler;
import com.builtbroken.bagableplants.handler.VanillaHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/3/2017.
 */
@Mod(modid = "bagableplants", name = "Bagable Plants", version = BagablePlants.VERSION)
public class BagablePlants
{
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    @SidedProxy(modId = "bagableplants", serverSide = "com.builtbroken.bagableplants.CommonProxy", clientSide = "com.builtbroken.bagableplants.client.ClientProxy")
    public static CommonProxy proxy;

    public static Item itemBag;

    /** Map of objects to handlers in minecraft */
    public static final HashMap<Object, InteractionHandler> blockNameToHandler = new HashMap();
    /** Map of items to blocks, corrects for items that are not ItemBlocks (ex. reeds) */
    public static final HashMap<Item, Block> itemToBlockMap = new HashMap();

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
            Block block = (Block) Block.REGISTRY.getObject(name);
            if (block != null)
            {
                blockNameToHandler.put(block, handler);
                return true;
            }
        }
        return false;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        itemBag = new ItemBag();
        itemBag.setRegistryName("bpPlantBag");
        GameRegistry.register(itemBag);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        VanillaHandler.register();
        if (Loader.isModLoaded("DCsAppleMilk"))
        {
            //AppleTeaMilkHandler.register();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GameRegistry.addShapedRecipe(new ItemStack(itemBag), "LSL", "LBL", " L ", 'L', Items.LEATHER, 'B', Items.BOWL, 'S', Items.STRING);
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
