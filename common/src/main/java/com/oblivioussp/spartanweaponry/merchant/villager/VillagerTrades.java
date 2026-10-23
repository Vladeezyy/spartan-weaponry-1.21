package com.oblivioussp.spartanweaponry.merchant.villager;

import net.minecraft.world.item.Item;

/** 26.1: the trades are data (data/minecraft/villager_trade, tags/villager_trade); the weaponsmith lists stay here. */
public class VillagerTrades 
{
	public static class RandomisedTradeItem
	{
		private Item item;
		private int emeraldCost;
		
		public RandomisedTradeItem(Item item, int emeraldCost)
		{
			this.item = item;
			this.emeraldCost = emeraldCost;
		}
		
		public Item getItem()
		{
			return item;
		}
		
		public int getEmeraldCost()
		{
			return emeraldCost;
		}
	}
}
