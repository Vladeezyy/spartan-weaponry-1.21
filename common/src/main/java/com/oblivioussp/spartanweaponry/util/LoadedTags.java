package com.oblivioussp.spartanweaponry.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

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
}
