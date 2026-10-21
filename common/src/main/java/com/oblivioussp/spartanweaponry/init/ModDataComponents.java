package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemContainerContents;

public class ModDataComponents
{
	public static final Registrar<DataComponentType<?>> REGISTRY = Registrar.create(Registries.DATA_COMPONENT_TYPE);

	/** The items inside a quiver (1.20.1: the quiver's item handler capability, saved to its "Ammo" NBT) */
	public static final Registrar.Entry<DataComponentType<?>, DataComponentType<ItemContainerContents>> QUIVER_CONTENTS = REGISTRY.registerComponentType("quiver_contents",
			(builder) -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding());
}
