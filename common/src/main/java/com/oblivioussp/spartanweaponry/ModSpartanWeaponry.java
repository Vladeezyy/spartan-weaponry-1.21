package com.oblivioussp.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.oblivioussp.spartanweaponry.api.trait.MeleeBlockWeaponTrait;
import com.oblivioussp.spartanweaponry.client.ClientHelper;
import com.oblivioussp.spartanweaponry.init.ModBlockEntities;
import com.oblivioussp.spartanweaponry.init.ModBlocks;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.init.ModCommands;
import com.oblivioussp.spartanweaponry.init.ModCreativeTabs;
import com.oblivioussp.spartanweaponry.init.ModCriteriaTriggers;
import com.oblivioussp.spartanweaponry.init.ModEntities;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.init.ModLootModifiers;
import com.oblivioussp.spartanweaponry.init.ModMenus;
import com.oblivioussp.spartanweaponry.init.ModMobEffects;
import com.oblivioussp.spartanweaponry.init.ModOilRecipes;
import com.oblivioussp.spartanweaponry.init.ModParticles;
import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;
import com.oblivioussp.spartanweaponry.init.ModSounds;
import com.oblivioussp.spartanweaponry.network.NetworkHandler;
import com.oblivioussp.spartanweaponry.util.ClientConfig;
import com.oblivioussp.spartanweaponry.util.Config;
import com.oblivioussp.spartanweaponry.util.InternalAPIMethodHandler;
import com.oblivioussp.spartanweaponry.util.Log;

import com.oblivioussp.spartanweaponry.init.ModDataComponents;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(value = ModSpartanWeaponry.ID)
public class ModSpartanWeaponry 
{
	// Mod information
	public static final String ID = "spartanweaponry";
	public static final String NAME = "Spartan Weaponry";
	
	public ModSpartanWeaponry(IEventBus modBus, ModContainer container)
	{
		Log.info("Constructing Mod: " + NAME);
		Log.info("Initialising API! Version: " + SpartanWeaponryAPI.API_VERSION);
		SpartanWeaponryAPI.init(new InternalAPIMethodHandler());
		
		modBus.addListener(this::onSetup);
		modBus.addListener(this::onClientSetup);
		
		// Registering Deferred Registries
		ModBlocks.REGISTRY.register(modBus);
		ModItems.REGISTRY.register(modBus);
		ModCreativeTabs.REGISTRY.register(modBus);
		ModEntities.REGISTRY.register(modBus);
		ModBlockEntities.REGISTRY.register(modBus);
		ModDataComponents.REGISTRY.register(modBus);
		ModCriteriaTriggers.REGISTRY.register(modBus);
		ModRecipeSerializers.REGISTRY.register(modBus);
		ModRecipeSerializers.CONDITION_REGISTRY.register(modBus);
		ModMenus.REGISTRY.register(modBus);
		ModSounds.REGISTRY.register(modBus);
		ModParticles.REGISTRY.register(modBus);
		ModLootModifiers.REGISTRY.register(modBus);
		WeaponTraits.REGISTRY.register(modBus);
		ModMobEffects.REGISTRY.register(modBus);
		OilEffects.REGISTRY.register(modBus);
		
		modBus.addListener(ModCapabilities::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(MeleeBlockWeaponTrait::onBlockEvent);
		modBus.addListener(RegisterPayloadHandlersEvent.class, NetworkHandler::register);
		ModLootModifiers.CONDITION_REGISTRY.register(modBus);
		ModCommands.ARGUMENT_REGISTRY.register(modBus);
		NeoForge.EVENT_BUS.addListener(ModCommands::registerCommands);
		NeoForge.EVENT_BUS.addListener(ModOilRecipes::initOilRecipes);
        
        // Place Config registration here...
        container.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG_SPEC);
//        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.CONFIG_SPEC);
        // Register extension points
//        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ConfigScreen::new);
	}
	
	private void onSetup(FMLCommonSetupEvent ev)
    {
        Log.info("Setting up " + NAME + "!");
        ev.enqueueWork(() ->
        {
        	Config.applyRegistryValues();
        });
    }

	private void onClientSetup(FMLClientSetupEvent ev)
    {
        Log.info("Setting up Client for " + NAME + "!");
        ev.enqueueWork(() -> 
        {
            ClientHelper.registerCurioRenders();
            ClientHelper.registerSkullTextures();
            ClientHelper.registerPendingItemProperties();
        });
    }
}
