package com.oblivioussp.spartanweaponry.event;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import java.util.List;

import com.oblivioussp.spartanweaponry.api.IReloadable;
import com.oblivioussp.spartanweaponry.api.ReloadableHandler;
import com.oblivioussp.spartanweaponry.api.WeaponMaterial;
import com.oblivioussp.spartanweaponry.init.ModOilRecipes;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.WeaponArchetype;


public class ReloadResourceEventHandler
{
	/** After the tags are (re)loaded, on the server and on the client. */
	public static void onUpdateTags()
	{
		List<WeaponMaterial> materialReloadList = ReloadableHandler.getMaterialReloadList();
		List<IReloadable> itemReloadList = ReloadableHandler.getItemReloadList();
		
		Log.debug("Initaliasing reloadables for " + materialReloadList.size() + " materials, " + WeaponArchetype.ALL_ARCHETYPES.size() + " archetypes and " + itemReloadList.size() + " items");
		long start = System.nanoTime();
		// Enforce an order of materials being reloaded first to ensure that items can fetch the appropriate traits from their materials
		// to prevent NullPointerExceptions!
		materialReloadList.forEach((material) -> material.reload());
		WeaponArchetype.ALL_ARCHETYPES.forEach((archetype) -> archetype.reload());
		itemReloadList.forEach((item) -> item.reload());
		long end = System.nanoTime();
		double milliseconds = (end-start) / 1000000.0d;
		ModOilRecipes.loadOilMixes();
		Log.info("Finished initialising Weapon Traits & Attributes! Took " + milliseconds + "ms");
	}
}
