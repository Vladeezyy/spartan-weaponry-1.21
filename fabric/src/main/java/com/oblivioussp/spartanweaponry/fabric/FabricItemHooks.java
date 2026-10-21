package com.oblivioussp.spartanweaponry.fabric;

import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.item.LongbowItem;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;

/** NeoForge's IItemExtension methods of the weapons, for the Fabric mixins and events. */
public final class FabricItemHooks
{
	private FabricItemHooks() {}

	/** IItemExtension#getMaxDamage, or -1 for other items. */
	public static int getMaxDamage(ItemStack stack)
	{
		Item item = stack.getItem();
		if(item instanceof SwordBaseItem weapon)
			return weapon.getMaxDamage(stack);
		if(item instanceof ThrowingWeaponItem weapon)
			return weapon.getMaxDamage(stack);
		if(item instanceof LongbowItem weapon)
			return weapon.getMaxDamage(stack);
		if(item instanceof HeavyCrossbowItem weapon)
			return weapon.getMaxDamage(stack);
		return -1;
	}

	/** IItemExtension#getDefaultAttributeModifiers(ItemStack), or null for other items. */
	public static ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		Item item = stack.getItem();
		if(item instanceof SwordBaseItem weapon)
			return weapon.getDefaultAttributeModifiers(stack);
		if(item instanceof ThrowingWeaponItem weapon)
			return weapon.getDefaultAttributeModifiers(stack);
		if(item instanceof LongbowItem weapon)
			return weapon.getDefaultAttributeModifiers(stack);
		if(item instanceof HeavyCrossbowItem weapon)
			return weapon.getDefaultAttributeModifiers(stack);
		return null;
	}

	/** IItemExtension#damageItem: the durability the stack really loses. */
	public static int damageItem(ItemStack stack, int amount, net.minecraft.world.entity.LivingEntity entity)
	{
		Item item = stack.getItem();
		if(item instanceof SwordBaseItem weapon)
			return weapon.damageItem(stack, amount, entity, i -> {});
		if(item instanceof ThrowingWeaponItem weapon)
			return weapon.damageItem(stack, amount, entity, i -> {});
		if(item instanceof LongbowItem weapon)
			return weapon.damageItem(stack, amount, entity, i -> {});
		if(item instanceof HeavyCrossbowItem weapon)
			return weapon.damageItem(stack, amount, entity, i -> {});
		return amount;
	}

	/** IItemExtension#isPrimaryItemFor (enchanting table) and #supportsEnchantment (anvil, /enchant). */
	public static TriState allowEnchanting(ItemStack stack, Holder<Enchantment> enchantment, boolean primary)
	{
		Item item = stack.getItem();
		if(item instanceof SwordBaseItem weapon)
			return TriState.of(primary ? weapon.isPrimaryItemFor(stack, enchantment) : weapon.supportsEnchantment(stack, enchantment));
		if(item instanceof ThrowingWeaponItem weapon)
			return TriState.of(primary ? weapon.isPrimaryItemFor(stack, enchantment) : weapon.supportsEnchantment(stack, enchantment));
		return TriState.DEFAULT;
	}
}
