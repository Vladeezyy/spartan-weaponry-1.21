package com.oblivioussp.spartanweaponry.init;

import com.mojang.serialization.MapCodec;
import com.oblivioussp.spartanweaponry.loot.ConfigLootCondition;
import com.oblivioussp.spartanweaponry.loot.TradeEnabledCondition;
import com.oblivioussp.spartanweaponry.loot.WeaponsmithTradeItemFunction;
import com.oblivioussp.spartanweaponry.platform.Registrar;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ModLootModifiers 
{
	
	// Loot Modifiers

	// Loot Conditions (26.1: the registry holds the codecs, LootItemConditionType is gone)
	public static final Registrar<MapCodec<? extends LootItemCondition>> CONDITION_REGISTRY = Registrar.create(Registries.LOOT_CONDITION_TYPE);
	public static final Registrar.Entry<MapCodec<? extends LootItemCondition>, MapCodec<ConfigLootCondition>> CONFIG_ENABLED = CONDITION_REGISTRY.register("new_heads_enabled", () -> ConfigLootCondition.CODEC);
	public static final Registrar.Entry<MapCodec<? extends LootItemCondition>, MapCodec<TradeEnabledCondition>> TRADE_ENABLED = CONDITION_REGISTRY.register("trade_enabled", () -> TradeEnabledCondition.CODEC);

	// Loot Functions (26.1: the villager trades are data, data/minecraft/tags/villager_trade)
	public static final Registrar<MapCodec<? extends LootItemFunction>> FUNCTION_REGISTRY = Registrar.create(Registries.LOOT_FUNCTION_TYPE);
	public static final Registrar.Entry<MapCodec<? extends LootItemFunction>, MapCodec<WeaponsmithTradeItemFunction>> WEAPONSMITH_TRADE_ITEM = FUNCTION_REGISTRY.register("weaponsmith_trade_item", () -> WeaponsmithTradeItemFunction.CODEC);
}
