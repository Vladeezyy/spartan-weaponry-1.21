package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents
{
	public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModSpartanWeaponry.ID);

	/** The items inside a quiver (1.20.1: the quiver's item handler capability, saved to its "Ammo" NBT) */
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> QUIVER_CONTENTS = REGISTRY.registerComponentType("quiver_contents",
			(builder) -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding());
}
