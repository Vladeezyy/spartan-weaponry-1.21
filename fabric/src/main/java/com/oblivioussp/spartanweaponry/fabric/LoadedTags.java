package com.oblivioussp.spartanweaponry.fabric;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * The tags of the data pack reload in progress, with their entries (filled by TagLoadResultMixin). Fabric's own
 * fabric:tags_populated only checks that a tag file exists, even an empty one; NeoForge's tag_empty looks at the
 * entries, and so does {@link TagNotEmptyResourceCondition}.
 */
public final class LoadedTags
{
	private static final Map<ResourceKey<?>, Map<ResourceLocation, ? extends Collection<?>>> TAGS = new ConcurrentHashMap<>();

	private LoadedTags() {}

	public static void put(ResourceKey<?> registry, Map<ResourceLocation, ? extends Collection<?>> tags)
	{
		TAGS.put(registry, tags);
	}

	public static boolean isPopulated(ResourceKey<?> registry, ResourceLocation tag)
	{
		Map<ResourceLocation, ? extends Collection<?>> tags = TAGS.get(registry);
		if(tags == null)
			return false;
		Collection<?> entries = tags.get(tag);
		return entries != null && !entries.isEmpty();
	}
}
