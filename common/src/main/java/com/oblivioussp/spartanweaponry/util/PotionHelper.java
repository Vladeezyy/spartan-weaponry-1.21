package com.oblivioussp.spartanweaponry.util;

import net.minecraft.core.registries.Registries;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

/**
 * Potions of 1.20.1's PotionUtils. A potion on a projectile item is the vanilla {@code minecraft:potion_contents}
 * component (like tipped arrows); the "Potion" key of 1.20.1 item NBT (now in custom_data) is still read. Oils keep
 * their potion as "Potion" inside their own "Oil" compound, as in 1.20.1.
 */
public final class PotionHelper
{
	/** PotionUtils.TAG_POTION */
	public static final String TAG_POTION = "Potion";

	private PotionHelper() {}

	public static Optional<Holder<Potion>> getPotion(CompoundTag tag)
	{
		if(!tag.contains(TAG_POTION))
			return Optional.empty();
		Identifier id = Identifier.tryParse(tag.getStringOr(TAG_POTION, ""));
		return id == null ? Optional.empty() : BuiltInRegistries.POTION.getHolder(id).map((ref) -> (Holder<Potion>)ref);
	}

	public static void putPotion(CompoundTag tag, Holder<Potion> potion)
	{
		potion.unwrapKey().ifPresent((key) -> tag.putString(TAG_POTION, key.location().toString()));
	}

	public static List<MobEffectInstance> getEffects(CompoundTag tag)
	{
		return getPotion(tag).map((potion) -> potion.value().getEffects()).orElse(List.of());
	}

	/** The potion contents of an item stack: the component, or the 1.20.1 "Potion" NBT key */
	public static PotionContents getContents(ItemStack stack)
	{
		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		if(contents != null)
			return contents;
		return getPotion(ItemNbt.read(stack)).map(PotionContents::new).orElse(PotionContents.EMPTY);
	}

	public static Optional<Holder<Potion>> getPotion(ItemStack stack)
	{
		return getContents(stack).potion();
	}

	public static ItemStack setPotion(ItemStack stack, Holder<Potion> potion)
	{
		stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
		return stack;
	}

	public static int getColor(ItemStack stack)
	{
		return getContents(stack).getColor();
	}

	public static void addPotionTooltip(ItemStack stack, List<Component> tooltip, float durationFactor, float ticksPerSecond)
	{
		getContents(stack).addPotionTooltip(tooltip::add, durationFactor, ticksPerSecond);
	}

	public static String getId(Holder<Potion> potion)
	{
		return potion.unwrapKey().map((key) -> key.location().toString()).orElse("");
	}
}
