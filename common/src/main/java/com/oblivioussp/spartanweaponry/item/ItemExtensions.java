package com.oblivioussp.spartanweaponry.item;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * The default bodies of NeoForge's IItemExtension methods, for the super calls of the common items. The items declare
 * those methods without {@code @Override}: on NeoForge they override the extension, on Fabric mixins call them.
 */
public final class ItemExtensions
{
	private ItemExtensions() {}

	public static boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment)
	{
		return stack.is(Items.ENCHANTED_BOOK) || enchantment.value().isSupportedItem(stack);
	}

	/** IItemExtension#isPrimaryItemFor; supported is the item's own supportsEnchantment for the stack. */
	public static boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment, boolean supported)
	{
		if(stack.getItem() == Items.BOOK)
			return true;
		Optional<HolderSet<Item>> primaryItems = enchantment.value().definition().primaryItems();
		return supported && (primaryItems.isEmpty() || stack.is(primaryItems.get()));
	}
}
