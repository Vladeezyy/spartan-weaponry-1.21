package com.oblivioussp.spartanweaponry.init;

import javax.annotation.Nullable;

import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.capability.IQuiverItemHandler;
import com.oblivioussp.spartanweaponry.capability.OilHandler;
import com.oblivioussp.spartanweaponry.capability.QuiverItemStackHandler;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;

import net.minecraft.world.item.ItemStack;

/**
 * The oil coating of a weapon and the inventory of a quiver. The NeoForge port had them as item capabilities; both
 * only live in the stack's components, so a view is made on demand (the same on NeoForge and Fabric).
 */
public class ModCapabilities
{
	/** Oil: any item in the "oilable_weapons" tag (1.20.1: AttachCapabilitiesEvent per stack). */
	@Nullable
	public static IOilHandler getOil(ItemStack stack)
	{
		return stack.is(ModItemTags.OILABLE_WEAPONS) ? new OilHandler(stack) : null;
	}

	@Nullable
	public static IQuiverItemHandler getQuiver(ItemStack stack)
	{
		return stack.getItem() instanceof QuiverBaseItem quiver ? new QuiverItemStackHandler(stack, quiver.getAmmoSlots()) : null;
	}
}
