package com.oblivioussp.spartanweaponry.neoforge;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.util.LoadedTags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * spartanweaponry:tag_not_empty: the item tag has at least one entry (as on Fabric). NeoForge 21.11's
 * {@code not tag_empty} only asks whether the tag exists, and NeoForge itself declares the common ingot tags of
 * other mods' metals (c:ingots/tin ...) empty, so those recipes loaded without the metal. The entries come from the
 * tags of the reload (LoadedTags); the condition context only knows which tags exist.
 */
public record TagNotEmptyConditionNeoForge(TagKey<Item> tag) implements ICondition
{
	public static final MapCodec<TagNotEmptyConditionNeoForge> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
			.group(Identifier.CODEC.fieldOf("tag").forGetter(c -> c.tag().location()))
			.apply(builder, id -> new TagNotEmptyConditionNeoForge(TagKey.create(Registries.ITEM, id))));

	@Override
	public boolean test(IContext context)
	{
		Boolean populated = LoadedTags.isPopulated(tag);
		return populated != null ? populated : context.isTagLoaded(tag);
	}

	@Override
	public MapCodec<? extends ICondition> codec()
	{
		return CODEC;
	}
}
