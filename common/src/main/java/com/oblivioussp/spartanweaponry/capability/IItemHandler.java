package com.oblivioussp.spartanweaponry.capability;

import net.minecraft.world.item.ItemStack;

/**
 * An item inventory with slots (the methods of NeoForge's IItemHandler, which the 1.21.1 NeoForge port used; kept
 * loader-neutral so that the quiver code runs on NeoForge and Fabric alike).
 */
public interface IItemHandler
{
	int getSlots();

	/** The stack in the slot; do not change it (use {@link #insertItem} / {@link #extractItem}). */
	ItemStack getStackInSlot(int slot);

	/** Puts the stack into the slot, returns what did not fit. */
	ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

	/** Takes up to {@code amount} items out of the slot. */
	ItemStack extractItem(int slot, int amount, boolean simulate);

	int getSlotLimit(int slot);

	boolean isItemValid(int slot, ItemStack stack);

	void setStackInSlot(int slot, ItemStack stack);
}
