package com.oblivioussp.spartanweaponry.inventory;

import java.util.function.Predicate;


import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import com.oblivioussp.spartanweaponry.capability.IItemHandler;

public class SlotFiltered extends SlotItemHandler
{
	protected final Predicate<ItemStack> filter;
	private Identifier background;

	public SlotFiltered(IItemHandler handlerIn, int indexIn, int xPositionIn, int yPositionIn, Predicate<ItemStack> filterIn)
	{
		super(handlerIn, indexIn, xPositionIn, yPositionIn);
		filter = filterIn;
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return filter.test(stack);
	}

	/** The picture of the empty slot, a sprite of the GUI atlas (1.21.2+; NeoForge's Slot#setBackground) */
	public SlotFiltered setBackground(Identifier sprite)
	{
		background = sprite;
		return this;
	}

	@Override
	public Identifier getNoItemIcon()
	{
		return background;
	}
}
