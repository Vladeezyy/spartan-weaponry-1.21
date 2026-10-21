package com.oblivioussp.spartanweaponry.util;

import net.minecraft.core.registries.Registries;
import java.util.Map;

import com.google.common.collect.Multimap;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * The weapons' attribute modifiers. 1.20.1 returned them from getAttributeModifiers(slot, stack) for the main hand;
 * since 1.20.5 an item has an attribute component, but these values change on every reload (config and
 * weapon_attributes data), so the items build them on reload and return them from getDefaultAttributeModifiers.
 */
public final class WeaponAttributes
{
	private WeaponAttributes() {}

	public static ItemAttributeModifiers toItemModifiers(Multimap<Holder<Attribute>, AttributeModifier> modifiers)
	{
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		for(Map.Entry<Holder<Attribute>, AttributeModifier> entry : modifiers.entries())
			builder.add(entry.getKey(), entry.getValue(), EquipmentSlotGroup.MAINHAND);
		return builder.build();
	}

	/**
	 * The modifiers of the weapon unless the stack carries its own (an attribute_modifiers component set on the stack,
	 * e.g. by a command: the items have no default one)
	 */
	public static ItemAttributeModifiers forStack(ItemStack stack, ItemAttributeModifiers weaponModifiers, ItemAttributeModifiers componentModifiers)
	{
		if(!componentModifiers.modifiers().isEmpty())
			return componentModifiers;
		return weaponModifiers;
	}

	/** {@code ITagManager.isKnownTagName(tag) && !getTag(tag).isEmpty()} of 1.20.1 */
	public static boolean isTagFilled(TagKey<Item> tag)
	{
		return BuiltInRegistries.ITEM.getTag(tag).map((set) -> set.size() > 0).orElse(false);
	}
}
