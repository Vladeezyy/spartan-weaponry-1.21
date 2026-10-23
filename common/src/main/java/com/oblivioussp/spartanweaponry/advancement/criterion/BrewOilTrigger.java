package com.oblivioussp.spartanweaponry.advancement.criterion;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.api.oil.OilEffect;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

/** Triggers when a player takes a brewed oil out of a brewing stand ({@code "oil_effect"}: an oil id, optional) */
public class BrewOilTrigger extends SimpleCriterionTrigger<BrewOilTrigger.TriggerInstance> 
{
	@Override
	public Codec<TriggerInstance> codec()
	{
		return TriggerInstance.CODEC;
	}
	
	public void trigger(ServerPlayer playerIn, OilEffect oilEffectIn)
	{
		trigger(playerIn, (triggerInstance) -> triggerInstance.matches(oilEffectIn));
	}
	
	// 26.3: the player is a loot condition (no ContextAwarePredicate)
	public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<OilEffect> oilEffect) implements SimpleCriterionTrigger.SimpleInstance
	{
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
				OilEffects.OIL_REGISTRY.byNameCodec().optionalFieldOf("oil_effect").forGetter(TriggerInstance::oilEffect))
				.apply(instance, TriggerInstance::new));
		
		public static TriggerInstance brewedOil()
		{
			return new TriggerInstance(Optional.empty(), Optional.empty());
		}
		
		public boolean matches(OilEffect oilEffectIn)
		{
			return oilEffect.isEmpty() || oilEffect.get() == oilEffectIn;
		}
	}
}
