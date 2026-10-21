package com.oblivioussp.spartanweaponry.inventory;

import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.oblivioussp.spartanweaponry.capability.IItemHandler;

public class SlotFiltered extends SlotItemHandler
{
	protected final Predicate<ItemStack> filter;
	private Pair<ResourceLocation, ResourceLocation> background;

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

	/** The picture of the empty slot (NeoForge's Slot#setBackground). */
	public SlotFiltered setBackground(ResourceLocation atlas, ResourceLocation sprite)
	{
		background = Pair.of(atlas, sprite);
		return this;
	}

	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
	{
		return background;
	}
}
