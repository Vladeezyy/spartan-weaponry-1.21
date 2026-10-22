package com.oblivioussp.spartanweaponry.util;

import com.mojang.serialization.Dynamic;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;

/** Item stacks that 1.20.1 saved inside the mod's own item NBT (the game's data fixer does not reach them) */
public final class LegacyItems
{
	/** Data version of Minecraft 1.20.1 */
	public static final int DATA_VERSION_1_20_1 = 3465;

	private LegacyItems() {}

	/** Reads an item stack saved by 1.20.1 ({@code id}, {@code Count}, {@code tag}) */
	public static ItemStack parse(CompoundTag oldStack, HolderLookup.Provider registries)
	{
		Dynamic<?> updated = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, oldStack),
				DATA_VERSION_1_20_1, SharedConstants.getCurrentVersion().dataVersion().version());
		return ItemStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), (CompoundTag)updated.getValue()).result().orElse(ItemStack.EMPTY);
	}
}
