package com.oblivioussp.spartanweaponry.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;

/**
 * 1.21.5+: tooltips are written to a Consumer. The 1.21.1 tooltip code added to a List (and sometimes inserted at an
 * index or read it back), so the lines collect here and go to the consumer with {@link #flush()}, in order.
 */
public final class TooltipLines extends AbstractList<Component>
{
	private final List<Component> lines = new ArrayList<>();
	private final Consumer<Component> out;

	public TooltipLines(Consumer<Component> out)
	{
		this.out = out;
	}

	@Override
	public Component get(int index)
	{
		return lines.get(index);
	}

	@Override
	public int size()
	{
		return lines.size();
	}

	@Override
	public void add(int index, Component element)
	{
		lines.add(index, element);
	}

	@Override
	public Component set(int index, Component element)
	{
		return lines.set(index, element);
	}

	@Override
	public Component remove(int index)
	{
		return lines.remove(index);
	}

	/** Gives the collected lines to the consumer */
	public void flush()
	{
		lines.forEach(out);
		lines.clear();
	}
}
