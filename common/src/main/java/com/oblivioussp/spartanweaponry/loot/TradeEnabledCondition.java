package com.oblivioussp.spartanweaponry.loot;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.util.Config;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * The merchant predicate of the fletcher trades: villager trading on and the weapon's recipes not disabled (1.20.1
 * added the trades in VillagerTradesEvent only then; 26.1 trades are data, a false predicate skips the trade).
 * {"condition": "spartanweaponry:trade_enabled", "weapon": "longbow"}, weapon: longbow or heavy_crossbow.
 */
public record TradeEnabledCondition(Optional<String> weapon) implements LootItemCondition
{
	public static final MapCodec<TradeEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Codec.STRING.optionalFieldOf("weapon").forGetter(TradeEnabledCondition::weapon)).apply(i, TradeEnabledCondition::new));

	@Override
	public boolean test(LootContext context)
	{
		if(Config.INSTANCE.disableVillagerTrading.get())
			return false;
		return weapon.map(w -> switch(w)
		{
			case "longbow" -> !Config.INSTANCE.longbows.disableRecipes.get();
			case "heavy_crossbow" -> !Config.INSTANCE.heavyCrossbows.disableRecipes.get();
			default -> true;
		}).orElse(true);
	}

	@Override
	public MapCodec<TradeEnabledCondition> codec()
	{
		return CODEC;
	}
}
