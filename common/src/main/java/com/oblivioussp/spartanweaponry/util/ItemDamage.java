package com.oblivioussp.spartanweaponry.util;

import javax.annotation.Nullable;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/** Item damage as 1.20.1's ItemStack.hurt(amount, random, player) did it */
public final class ItemDamage
{
	private ItemDamage() {}

	/**
	 * Damages the stack (Unbreaking applies, the durability advancement triggers) without breaking it. Throwing
	 * weapons use this: a "broken" one uses up one ammo and gets its durability back.
	 * @return true if the damage reached the maximum (the item would break)
	 */
	public static boolean hurt(ItemStack stack, int amount, ServerLevel level, @Nullable ServerPlayer player)
	{
		if(!stack.isDamageableItem())
			return false;
		if(amount > 0)
		{
			amount = EnchantmentHelper.processDurabilityChange(level, stack, amount);
			if(amount <= 0)
				return false;
		}
		if(player != null && amount != 0)
			CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(player, stack, stack.getDamageValue() + amount);
		int damage = stack.getDamageValue() + amount;
		stack.setDamageValue(damage);
		return damage >= stack.getMaxDamage();
	}
}
