package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.loot.ConfigLootCondition;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ModLootModifiers 
{
	
	// Loot Modifiers

	// Loot Conditions
	public static final Registrar<LootItemConditionType> CONDITION_REGISTRY = Registrar.create(Registries.LOOT_CONDITION_TYPE);
	public static final Registrar.Entry<LootItemConditionType, LootItemConditionType> CONFIG_ENABLED = CONDITION_REGISTRY.register("new_heads_enabled", () -> new LootItemConditionType(ConfigLootCondition.CODEC));
}
