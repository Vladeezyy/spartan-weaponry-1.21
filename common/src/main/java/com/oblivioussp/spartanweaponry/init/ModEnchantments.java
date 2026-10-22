package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * The mod's enchantments. Since 1.21 enchantments are data: data/spartanweaponry/enchantment/*.json (costs, weights,
 * levels, supported items and exclusive sets from the 1.20.1 classes); their effects stay in the mod's code.
 */
public class ModEnchantments 
{	
	public static final ResourceKey<Enchantment> PROPEL = key("propel");
	public static final ResourceKey<Enchantment> RAZORS_EDGE = key("razors_edge");
	public static final ResourceKey<Enchantment> INCENDIARY = key("incendiary");
	public static final ResourceKey<Enchantment> LUCKY_THROW = key("lucky_throw");
	public static final ResourceKey<Enchantment> HYDRODYNAMIC = key("hydrodynamic");
	public static final ResourceKey<Enchantment> SUPERCHARGE = key("supercharge");
	public static final ResourceKey<Enchantment> EXPANSE = key("expanse");
	public static final ResourceKey<Enchantment> SHARPSHOOTER = key("sharpshooter");
	public static final ResourceKey<Enchantment> COLLECTORANG = key("collectorang");
	
	private static ResourceKey<Enchantment> key(String name)
	{
		return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, name));
	}
}
