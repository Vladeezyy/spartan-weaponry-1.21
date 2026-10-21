package com.oblivioussp.spartanweaponry.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * An {@link IItemHandler} over an {@link ItemContainerContents} component of an item stack (what NeoForge's
 * ComponentItemHandler does, written against vanilla so that Fabric has it too). Every change writes a new component
 * value: the component is immutable, as vanilla expects.
 */
public class ComponentItemHandler implements IItemHandler
{
	protected final ItemStack parent;
	protected final DataComponentType<ItemContainerContents> component;
	protected final int size;

	public ComponentItemHandler(ItemStack parent, DataComponentType<ItemContainerContents> component, int size)
	{
		this.parent = parent;
		this.component = component;
		this.size = size;
	}

	protected NonNullList<ItemStack> getItems()
	{
		NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
		parent.getOrDefault(component, ItemContainerContents.EMPTY).copyInto(items);
		return items;
	}

	protected void setItems(NonNullList<ItemStack> items)
	{
		parent.set(component, ItemContainerContents.fromItems(items));
		onContentsChanged();
	}

	/** Called after every change. */
	protected void onContentsChanged() {}

	@Override
	public int getSlots()
	{
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		validateSlotIndex(slot);
		return getItems().get(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		validateSlotIndex(slot);
		NonNullList<ItemStack> items = getItems();
		items.set(slot, stack.copy());
		setItems(items);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if(stack.isEmpty())
			return ItemStack.EMPTY;
		if(!isItemValid(slot, stack))
			return stack;
		validateSlotIndex(slot);
		NonNullList<ItemStack> items = getItems();
		ItemStack existing = items.get(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		if(!existing.isEmpty())
		{
			if(!ItemStack.isSameItemSameComponents(stack, existing))
				return stack;
			limit -= existing.getCount();
		}
		if(limit <= 0)
			return stack;
		boolean reachedLimit = stack.getCount() > limit;
		if(!simulate)
		{
			if(existing.isEmpty())
				items.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack.copy());
			else
				items.set(slot, existing.copyWithCount(existing.getCount() + (reachedLimit ? limit : stack.getCount())));
			setItems(items);
		}
		return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if(amount == 0)
			return ItemStack.EMPTY;
		validateSlotIndex(slot);
		NonNullList<ItemStack> items = getItems();
		ItemStack existing = items.get(slot);
		if(existing.isEmpty())
			return ItemStack.EMPTY;
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if(existing.getCount() <= toExtract)
		{
			if(!simulate)
			{
				items.set(slot, ItemStack.EMPTY);
				setItems(items);
			}
			return existing;
		}
		if(!simulate)
		{
			items.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
			setItems(items);
		}
		return existing.copyWithCount(toExtract);
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return Item.ABSOLUTE_MAX_STACK_SIZE;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return stack.getItem().canFitInsideContainerItems();
	}

	protected void validateSlotIndex(int slot)
	{
		if(slot < 0 || slot >= size)
			throw new IndexOutOfBoundsException("Slot " + slot + " not in valid range - [0," + size + ")");
	}
}
