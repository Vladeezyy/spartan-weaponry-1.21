package com.oblivioussp.spartanweaponry.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * The tags of the reload in progress, for the tag_not_empty recipe condition (mixin/MappedRegistryMixin). 1.21.2+
 * loads the tags before the recipes, but the named sets of a first load stay unbound until the reload applies them,
 * so the registry lookup the conditions get knows which tags exist, not what is in them.
 */
public final class LoadedTags
{
	private static final Map<ResourceKey<?>, Map<? extends TagKey<?>, ? extends List<?>>> TAGS = new ConcurrentHashMap<>();

	private LoadedTags() {}

	public static void put(ResourceKey<?> registry, Map<? extends TagKey<?>, ? extends List<?>> tags)
	{
		TAGS.put(registry, tags);
	}

	/** Null when no reload has loaded tags of that registry yet */
	public static Boolean isPopulated(TagKey<?> tag)
	{
		Map<? extends TagKey<?>, ? extends List<?>> tags = TAGS.get(tag.registry());
		if(tags == null)
			return null;
		List<?> entries = tags.get(tag);
		return entries != null && !entries.isEmpty();
	}

	/**
	 * The first item of an item tag in the reload in progress (the result of the tag cooking recipes, read once when
	 * they load); the bound tag when no reload has loaded item tags yet. Empty for an empty or unknown tag.
	 */
	public static Optional<Item> firstItem(TagKey<Item> tag)
	{
		Map<? extends TagKey<?>, ? extends List<?>> tags = TAGS.get(tag.registry());
		if(tags != null)
		{
			List<?> entries = tags.get(tag);
			return entries == null || entries.isEmpty() ? Optional.empty() : Optional.of((Item)((Holder<?>)entries.getFirst()).value());
		}
		try
		{
			return BuiltInRegistries.ITEM.get(tag).flatMap((set) -> set.stream().findFirst()).map(Holder::value);
		}
		catch(IllegalStateException e)
		{
			// Tags not bound yet
			return Optional.empty();
		}
	}
}
