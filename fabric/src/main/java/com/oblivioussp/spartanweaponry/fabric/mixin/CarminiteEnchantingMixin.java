package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.oblivioussp.spartanweaponry.fabric.FabricItemHooks;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * The weapons' enchantment rules for carminite (the NeoForge layer of Twilight Forest on Fabric). Its mixins replace the
 * item checks of the enchanting table (EnchantmentHelper#getAvailableEnchantmentResults), /enchant and enchant_randomly
 * with Item#carminite$isPrimaryItemFor / #carminite$supportsEnchantment, so Fabric's EnchantmentEvents.ALLOW_ENCHANTING
 * (SpartanWeaponryFabric) is not asked there and e.g. Sweeping Edge showed up on a greatsword. These methods override
 * the defaults of its IItemExtension; without carminite nothing calls them.
 */
@Mixin({SwordBaseItem.class, ThrowingWeaponItem.class})
public abstract class CarminiteEnchantingMixin
{
	public boolean carminite$isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment)
	{
		return FabricItemHooks.allowEnchanting(stack, enchantment, true).orElse(false);
	}

	public boolean carminite$supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment)
	{
		return FabricItemHooks.allowEnchanting(stack, enchantment, false).orElse(false);
	}
}
