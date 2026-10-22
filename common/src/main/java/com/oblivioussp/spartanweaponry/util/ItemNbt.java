package com.oblivioussp.spartanweaponry.util;

import java.util.function.Consumer;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Item NBT of the 1.20.1 mod, kept in the {@code minecraft:custom_data} component under the same keys. Worlds from
 * 1.20.1 are upgraded by the game into exactly this form, so their weapons, quivers and oils keep their data.
 */
public final class ItemNbt
{
	private ItemNbt() {}

	/** A copy of the item's NBT (empty if it has none); changes need {@link #set} or {@link #update} */
	public static CompoundTag get(ItemStack stack)
	{
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	/**
	 * The item's NBT without a copy, for reading only: never change the returned tag (it is the component's own).
	 * {@link #get} copies the whole tag, which several per-tick and per-frame reads did for one value each.
	 */
	public static CompoundTag read(ItemStack stack)
	{
		// 1.21.11: no getUnsafe; the component's own tag through the access transformer / widener
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).tag;
	}

	/** {@code ItemStack.hasTag()} of 1.20.1 */
	public static boolean has(ItemStack stack)
	{
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && !data.isEmpty();
	}

	public static boolean contains(ItemStack stack, String key)
	{
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.tag.contains(key);
	}

	/** Replaces the item's NBT (an empty tag removes the component) */
	public static void set(ItemStack stack, CompoundTag tag)
	{
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
	}

	public static void update(ItemStack stack, Consumer<CompoundTag> updater)
	{
		CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
	}

	public static void remove(ItemStack stack, String key)
	{
		if(contains(stack, key))
			update(stack, (tag) -> tag.remove(key));
	}
}
