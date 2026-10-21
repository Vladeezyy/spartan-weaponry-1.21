package com.oblivioussp.spartanweaponry.util;

import net.minecraft.world.item.enchantment.ItemEnchantments;

import net.minecraft.core.component.DataComponents;

import java.util.Optional;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/** Enchantment levels by key (1.21 enchantments are registry data; these lookups need no registry access) */
public final class Enchants
{
	private Enchants() {}

	/** {@code stack.getEnchantmentLevel(enchantment)} of 1.20.1 */
	public static int level(ItemStack stack, ResourceKey<Enchantment> key)
	{
		for(Object2IntMap.Entry<Holder<Enchantment>> entry : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet())
		{
			if(entry.getKey().is(key))
				return entry.getIntValue();
		}
		return 0;
	}

	public static boolean is(Holder<Enchantment> holder, ResourceKey<Enchantment> key)
	{
		return holder.is(key);
	}

	public static Optional<Holder.Reference<Enchantment>> holder(HolderLookup.Provider registries, ResourceKey<Enchantment> key)
	{
		return registries.lookup(Registries.ENCHANTMENT).flatMap((lookup) -> lookup.get(key));
	}
}
