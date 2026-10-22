package com.oblivioussp.spartanweaponry.fabric;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

/** spartanweaponry:tag_not_empty: the item tag has at least one entry (NeoForge: not tag_empty). */
public record TagNotEmptyResourceCondition(Identifier tag) implements ResourceCondition
{
	public static final MapCodec<TagNotEmptyResourceCondition> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
			.group(Identifier.CODEC.fieldOf("tag").forGetter(TagNotEmptyResourceCondition::tag))
			.apply(builder, TagNotEmptyResourceCondition::new));
	public static final ResourceConditionType<TagNotEmptyResourceCondition> TYPE = ResourceConditionType.create(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "tag_not_empty"), CODEC);

	private static final ResourceKey<?> ITEMS = Registries.ITEM;

	@Override
	public ResourceConditionType<?> getType()
	{
		return TYPE;
	}

	@Override
	public boolean test(@Nullable HolderLookup.Provider registryLookup)
	{
		return LoadedTags.isPopulated(ITEMS, tag);
	}
}
