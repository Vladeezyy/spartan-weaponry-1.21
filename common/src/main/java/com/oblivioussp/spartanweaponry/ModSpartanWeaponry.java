package com.oblivioussp.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.oblivioussp.spartanweaponry.client.ClientHelper;
import com.oblivioussp.spartanweaponry.init.ModBlockEntities;
import com.oblivioussp.spartanweaponry.init.ModBlocks;
import com.oblivioussp.spartanweaponry.init.ModCreativeTabs;
import com.oblivioussp.spartanweaponry.init.ModCriteriaTriggers;
import com.oblivioussp.spartanweaponry.init.ModDataComponents;
import com.oblivioussp.spartanweaponry.init.ModEntities;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.init.ModLootModifiers;
import com.oblivioussp.spartanweaponry.init.ModMenus;
import com.oblivioussp.spartanweaponry.init.ModMobEffects;
import com.oblivioussp.spartanweaponry.init.ModParticles;
import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;
import com.oblivioussp.spartanweaponry.init.ModSounds;
import com.oblivioussp.spartanweaponry.platform.Platform;
import com.oblivioussp.spartanweaponry.util.Config;
import com.oblivioussp.spartanweaponry.util.InternalAPIMethodHandler;
import com.oblivioussp.spartanweaponry.util.Log;

/** The common part of the mod; the NeoForge and Fabric entry points call it. */
public class ModSpartanWeaponry
{
	// Mod information
	public static final String ID = "spartanweaponry";
	public static final String NAME = "Spartan Weaponry";

	/** Creates the registrars (loading the classes is enough); the loader registers their entries afterwards. */
	public static void init()
	{
		Log.info("Constructing Mod: " + NAME + " (" + Platform.INSTANCE.name() + ")");
		Log.info("Initialising API! Version: " + SpartanWeaponryAPI.API_VERSION);
		SpartanWeaponryAPI.init(new InternalAPIMethodHandler());

		ModBlocks.REGISTRY.getEntries();
		ModItems.REGISTRY.getEntries();
		ModCreativeTabs.REGISTRY.getEntries();
		ModEntities.REGISTRY.getEntries();
		ModBlockEntities.REGISTRY.getEntries();
		ModDataComponents.REGISTRY.getEntries();
		ModCriteriaTriggers.REGISTRY.getEntries();
		ModRecipeSerializers.REGISTRY.getEntries();
		ModMenus.REGISTRY.getEntries();
		ModSounds.REGISTRY.getEntries();
		ModParticles.REGISTRY.getEntries();
		ModLootModifiers.CONDITION_REGISTRY.getEntries();
		WeaponTraits.REGISTRY.getEntries();
		ModMobEffects.REGISTRY.getEntries();
		OilEffects.REGISTRY.getEntries();
	}

	/** After registration (NeoForge: common setup, on the main thread). */
	public static void setup()
	{
		Log.info("Setting up " + NAME + "!");
		Config.applyRegistryValues();
	}

	/** After registration on the client (NeoForge: client setup, on the main thread). */
	public static void clientSetup(ClientHelper.ItemPropertyRegistrar properties)
	{
		Log.info("Setting up Client for " + NAME + "!");
		ClientHelper.registerSkullTextures();
		ClientHelper.registerPendingItemProperties(properties);
	}
}
