package com.oblivioussp.spartanweaponry.capability;

import com.oblivioussp.spartanweaponry.init.ModDataComponents;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;

/** The inventory of a quiver, stored in its {@code spartanweaponry:quiver_contents} component */
public class QuiverItemStackHandler extends ComponentItemHandler implements IQuiverItemHandler
{
	public QuiverItemStackHandler(ItemStack quiver, int size)
	{
		super(quiver, ModDataComponents.QUIVER_CONTENTS.get(), size);
	}
	
	/**
	 * The size of a quiver comes from its item (an upgraded quiver is a new item with more slots). Kept for the API
	 * of 1.20.1; the contents of a smaller quiver carry over to the bigger one as they are.
	 */
	@Override
	public void resize(int size) {}

	// 1.20.1 returned true only when every slot was full; this is what the name says (fix of the port)
	@Override
	public boolean isEmpty() 
	{
		for(int i = 0; i < getSlots(); i++)
		{
			if(!getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	// Ammo is never a shulker box or bundle; the quiver menus decide what is valid
	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return true;
	}
}
