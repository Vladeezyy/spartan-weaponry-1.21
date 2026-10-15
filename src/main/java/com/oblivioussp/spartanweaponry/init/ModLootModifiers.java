package com.oblivioussp.spartanweaponry.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.loot.ConfigLootCondition;
import com.oblivioussp.spartanweaponry.loot.DecapitateLootModifier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModLootModifiers 
{
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModSpartanWeaponry.ID);
	
	// Loot Modifiers
	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<DecapitateLootModifier>> DECAPITATE = REGISTRY.register("decapitate", () -> DecapitateLootModifier.CODEC);

	// Loot Conditions
	public static final DeferredRegister<LootItemConditionType> CONDITION_REGISTRY = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ModSpartanWeaponry.ID);
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> CONFIG_ENABLED = CONDITION_REGISTRY.register("new_heads_enabled", () -> new LootItemConditionType(ConfigLootCondition.CODEC));
}
