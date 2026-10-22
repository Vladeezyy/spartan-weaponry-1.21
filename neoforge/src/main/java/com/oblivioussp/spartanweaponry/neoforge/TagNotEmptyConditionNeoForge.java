package com.oblivioussp.spartanweaponry.neoforge;

import java.lang.reflect.Field;
import java.util.Map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.util.Log;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ConditionContext;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * spartanweaponry:tag_not_empty: the item tag has at least one entry (as on Fabric). NeoForge 21.11's
 * {@code not tag_empty} only asks whether the tag exists, and NeoForge itself declares the common ingot tags of
 * other mods' metals (c:ingots/tin ...) empty, so those recipes loaded without the metal. The condition context keeps
 * the tags of the reload in a private field; without it, the tag counts as present when it is loaded.
 */
public record TagNotEmptyConditionNeoForge(TagKey<Item> tag) implements ICondition
{
	public static final MapCodec<TagNotEmptyConditionNeoForge> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
			.group(Identifier.CODEC.fieldOf("tag").forGetter(c -> c.tag().location()))
			.apply(builder, id -> new TagNotEmptyConditionNeoForge(TagKey.create(Registries.ITEM, id))));

	private static Field pendingTags;
	private static boolean failed;

	@Override
	public boolean test(IContext context)
	{
		HolderLookup.RegistryLookup<Item> items = pendingItems(context);
		if(items == null)
			return context.isTagLoaded(tag);
		return items.get(tag).map(set -> set.size() > 0).orElse(false);
	}

	@SuppressWarnings("unchecked")
	private static HolderLookup.RegistryLookup<Item> pendingItems(IContext context)
	{
		if(!(context instanceof ConditionContext) || failed)
			return null;
		try
		{
			if(pendingTags == null)
			{
				pendingTags = ConditionContext.class.getDeclaredField("pendingTags");
				pendingTags.setAccessible(true);
			}
			return (HolderLookup.RegistryLookup<Item>)((Map<?, ?>)pendingTags.get(context)).get(Registries.ITEM);
		}
		catch(ReflectiveOperationException | RuntimeException e)
		{
			failed = true;
			Log.error("tag_not_empty: cannot read the tags of the reload, falling back to 'tag exists': " + e);
			return null;
		}
	}

	@Override
	public MapCodec<? extends ICondition> codec()
	{
		return CODEC;
	}
}
