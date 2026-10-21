package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.ToolAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.world.item.ItemStack;

/**
 * An action a weapon can perform (NeoForge's ToolAction, by the same names; the NeoForge module maps one to the
 * other, Fabric checks the few that matter in mixins). One instance per name, so == compares them.
 */
public final class ToolAction
{
	private static final Map<String, ToolAction> ACTIONS = new ConcurrentHashMap<>();

	private final String name;

	private ToolAction(String name)
	{
		this.name = name;
	}

	public static ToolAction get(String name)
	{
		return ACTIONS.computeIfAbsent(name, ToolAction::new);
	}

	public String name()
	{
		return name;
	}

	/** Whether the stack's item can perform the action (only Spartan weapons know about these). */
	public static boolean canPerform(ItemStack stack, ToolAction action)
	{
		return stack.getItem() instanceof IToolActionItem item && item.canPerformToolAction(stack, action);
	}

	@Override
	public String toString()
	{
		return "ToolAction[" + name + "]";
	}

	/** An item that performs tool actions. */
	public interface IToolActionItem
	{
		boolean canPerformToolAction(ItemStack stack, ToolAction action);
	}
}
