package com.oblivioussp.spartanweaponry.merchant.villager;

import java.util.Optional;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;

import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;

public class VillagerTrades 
{
	public static class BaseTrade implements ItemListing
	{
		protected ItemStack wantedStack;
		protected ItemStack wantedStack2;
		protected ItemStack offeredStack;
		protected int maxUses;
		protected int xpGiven;	// Not to be confused with player XP
		protected float priceMultiplier;
		
		public BaseTrade(ItemStack wantedStack, ItemStack wantedStack2, ItemStack offeredStack, int maxUses, int xpGiven, float priceMultiplier)
		{
			this.wantedStack = wantedStack;
			this.wantedStack2 = wantedStack2;
			this.offeredStack = offeredStack;
			this.maxUses = maxUses;
			this.xpGiven = xpGiven;
			this.priceMultiplier = priceMultiplier;
		}

		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource rand)
		{
			return new MerchantOffer(cost(wantedStack), wantedStack2.isEmpty() ? Optional.empty() : Optional.of(cost(wantedStack2)), offeredStack.copy(), maxUses, xpGiven, priceMultiplier);
		}
	}
	
	public static class BuyItemWithEmeraldsTrade extends BaseTrade
	{
		public BuyItemWithEmeraldsTrade(int emeraldCost, ItemStack offeredStack, int maxUses,
				int xpGiven, float priceMultiplier) 
		{
			super(new ItemStack(Items.EMERALD, emeraldCost), ItemStack.EMPTY, offeredStack, maxUses, xpGiven, priceMultiplier);
		}
	}
	
	public static class BuyEnchantedItemWithEmeraldsTrade extends BuyItemWithEmeraldsTrade
	{

		public BuyEnchantedItemWithEmeraldsTrade(int emeraldCost, ItemStack offeredStack, int maxUses, int xpGiven,
				float priceMultiplier) 
		{
			super(emeraldCost, offeredStack, maxUses, xpGiven, priceMultiplier);
		}
		
		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource rand)
		{
			int level = 5 + rand.nextInt(15);
			ItemStack enchantedStack = enchant(trader, rand, new ItemStack(offeredStack.getItem()), level);
			return new MerchantOffer(cost(wantedStack), enchantedStack, maxUses, xpGiven, priceMultiplier);
		}
	}
	
	public static class RandomisedTradeItem
	{
		private ItemStack stack;
		private int emeraldCost;
		
		public RandomisedTradeItem(Item item, int emeraldCost)
		{
			this.stack = new ItemStack(item);
			this.emeraldCost = emeraldCost;
		}
		
		public ItemStack getItemStack()
		{
			return stack;
		}
		
		public int getEmeraldCost()
		{
			return emeraldCost;
		}
	}
	
	public static class RandomisedBuyItemWithEmeraldsTrade implements ItemListing
	{
		protected List<RandomisedTradeItem> offeredItems;
		protected int maxUses;
		protected int xpGiven;	// Not to be confused with player XP
		protected float priceMultiplier;

		public RandomisedBuyItemWithEmeraldsTrade(List<RandomisedTradeItem> items,
				int maxUses, int xpGiven, float priceMultiplier) 
		{
			this.offeredItems = items;
			this.maxUses = maxUses;
			this.xpGiven = xpGiven;
			this.priceMultiplier = priceMultiplier;
		}

		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource rand) 
		{
			// Port fix: nextInt(size) (1.20.1 used size - 1 and never offered the last item)
			RandomisedTradeItem offeredItem = offeredItems.get(rand.nextInt(offeredItems.size()));
			return new MerchantOffer(new ItemCost(Items.EMERALD, offeredItem.getEmeraldCost()), offeredItem.getItemStack().copy(), maxUses, xpGiven, priceMultiplier);
		}
		
	}
	
	public static class RandomisedBuyEnchantedItemWithEmeraldsTrade extends RandomisedBuyItemWithEmeraldsTrade
	{

		public RandomisedBuyEnchantedItemWithEmeraldsTrade(List<RandomisedTradeItem> items,
				int maxUses, int xpGiven, float priceMultiplier) 
		{
			super(items, maxUses, xpGiven, priceMultiplier);
		}
		
		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource rand)
		{
			// Port fix: nextInt(size) (1.20.1: size - 1 skipped the last item and crashed on a list of one)
			RandomisedTradeItem offeredItem = offeredItems.get(rand.nextInt(offeredItems.size()));
			int level = 5 + rand.nextInt(15);
			ItemStack enchantedStack = enchant(trader, rand, new ItemStack(offeredItem.getItemStack().getItem()), level);
			return new MerchantOffer(new ItemCost(Items.EMERALD, offeredItem.getEmeraldCost()), enchantedStack, maxUses, xpGiven, priceMultiplier);
		}
	}
	
	private static ItemCost cost(ItemStack stack)
	{
		return new ItemCost(stack.getItem(), stack.getCount());
	}
	
	/** Enchants like a villager's traded equipment (1.20.1: enchantItem(random, stack, level, false)) */
	private static ItemStack enchant(Entity trader, RandomSource rand, ItemStack stack, int level)
	{
		RegistryAccess registries = trader.level().registryAccess();
		Optional<HolderSet<Enchantment>> options = registries.registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.ON_TRADED_EQUIPMENT).map((tag) -> (HolderSet<Enchantment>)tag);
		return EnchantmentHelper.enchantItem(rand, stack, level, registries, options);
	}
}
