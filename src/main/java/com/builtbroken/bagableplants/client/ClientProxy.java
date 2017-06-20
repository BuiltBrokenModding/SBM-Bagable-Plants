package com.builtbroken.bagableplants.client;

import com.builtbroken.bagableplants.BagablePlants;
import com.builtbroken.bagableplants.CommonProxy;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/20/2017.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        ModelLoader.setCustomModelResourceLocation(BagablePlants.itemBag, 0, new ModelResourceLocation(BagablePlants.itemBag.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(BagablePlants.itemBag, 1, new ModelResourceLocation(BagablePlants.itemBag.getRegistryName() + "_filled", "inventory"));
    }
}
