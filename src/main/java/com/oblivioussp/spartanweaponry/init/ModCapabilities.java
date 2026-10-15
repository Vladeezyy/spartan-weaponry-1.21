package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.capability.IQuiverItemHandler;
import com.oblivioussp.spartanweaponry.capability.OilHandler;
import com.oblivioussp.spartanweaponry.capability.QuiverItemStackHandler;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities
{
	public static final ItemCapability<IOilHandler, Void> OIL_CAPABILITY = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil"), IOilHandler.class);
	public static final ItemCapability<IQuiverItemHandler, Void> QUIVER_ITEM_CAPABILITY = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_items"), IQuiverItemHandler.class);
	
	public static void registerCapabilities(RegisterCapabilitiesEvent ev)
	{
		// Oil: any item in the "oilable_weapons" tag, checked per stack like the 1.20.1 AttachCapabilitiesEvent did
		Item[] allItems = BuiltInRegistries.ITEM.stream().toArray(Item[]::new);
		ev.registerItem(OIL_CAPABILITY, (stack, context) -> stack.is(ModItemTags.OILABLE_WEAPONS) ? new OilHandler(stack) : null, allItems);
		// Quivers
		for(Item item : BuiltInRegistries.ITEM)
		{
			if(item instanceof QuiverBaseItem quiver)
				ev.registerItem(QUIVER_ITEM_CAPABILITY, (stack, context) -> new QuiverItemStackHandler(stack, quiver.getAmmoSlots()), item);
		}
	}
}
