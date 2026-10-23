package com.oblivioussp.spartanweaponry.loot;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.merchant.villager.VillagerTrades.RandomisedTradeItem;
import com.oblivioussp.spartanweaponry.merchant.villager.WeaponsmithTrades;
import com.oblivioussp.spartanweaponry.util.Config;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

/**
 * The weaponsmith trade of a level (1.20.1 RandomisedBuyItemWithEmeraldsTrade): a random weapon of the level's list
 * (WeaponsmithTrades, from the config), its emerald price as the additional trade cost on top of the trade's 0
 * emeralds. An empty list or villager trading off gives nothing, so the villager picks another trade.
 * {"function": "spartanweaponry:weaponsmith_trade_item", "level": 1}
 */
public record WeaponsmithTradeItemFunction(int level) implements LootItemFunction
{
	public static final MapCodec<WeaponsmithTradeItemFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Codec.intRange(1, 5).fieldOf("level").forGetter(WeaponsmithTradeItemFunction::level)).apply(i, WeaponsmithTradeItemFunction::new));

	@Override
	public ItemStack apply(ItemStack stack, LootContext context)
	{
		List<RandomisedTradeItem> items = WeaponsmithTrades.items(level);
		if(Config.INSTANCE.disableVillagerTrading.get() || items.isEmpty())
			return ItemStack.EMPTY;
		RandomisedTradeItem offered = items.get(context.getRandom().nextInt(items.size()));
		ItemStack result = new ItemStack(offered.getItem());
		result.set(DataComponents.ADDITIONAL_TRADE_COST, offered.getEmeraldCost());
		return result;
	}

	@Override
	public MapCodec<WeaponsmithTradeItemFunction> codec()
	{
		return CODEC;
	}
}
