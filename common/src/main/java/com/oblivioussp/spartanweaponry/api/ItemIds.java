package com.oblivioussp.spartanweaponry.api;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 1.21.2+: an item or block takes its id from its properties, before it is constructed. The registrar runs each
 * entry's factory inside {@link #creating}, so the API's weapon factories (and an addon's registrar that does the same)
 * get properties with the right id from {@link #properties()} without passing it through every factory.
 */
public final class ItemIds
{
	private static final ThreadLocal<ResourceKey<?>> CURRENT = new ThreadLocal<>();

	private ItemIds() {}

	/** Runs the factory of the registry entry with this key */
	public static <T> T creating(ResourceKey<?> key, Supplier<T> factory)
	{
		ResourceKey<?> previous = CURRENT.get();
		CURRENT.set(key);
		try
		{
			return factory.get();
		}
		finally
		{
			CURRENT.set(previous);
		}
	}

	/** The key of the registry entry being created (EntityType.Builder#build) */
	@SuppressWarnings("unchecked")
	public static <T> ResourceKey<T> key()
	{
		return (ResourceKey<T>)current();
	}

	/** New item properties with the id of the item being created */
	@SuppressWarnings("unchecked")
	public static Item.Properties properties()
	{
		return new Item.Properties().setId((ResourceKey<Item>)current());
	}

	/** The block properties with the id of the block being created */
	@SuppressWarnings("unchecked")
	public static <P extends BlockBehaviour.Properties> P block(P properties)
	{
		properties.setId((ResourceKey<Block>)current());
		return properties;
	}

	private static ResourceKey<?> current()
	{
		ResourceKey<?> key = CURRENT.get();
		if(key == null)
			throw new IllegalStateException("Item and block properties need the id of the registry entry: create them inside ItemIds.creating (the registrar's factory)");
		return key;
	}
}
