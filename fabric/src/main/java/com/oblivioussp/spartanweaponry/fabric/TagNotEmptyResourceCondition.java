package com.oblivioussp.spartanweaponry.fabric;

import com.oblivioussp.spartanweaponry.util.LoadedTags;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

/** spartanweaponry:tag_not_empty: the item tag has at least one entry (NeoForge: not tag_empty). */
public record TagNotEmptyResourceCondition(Identifier tag) implements ResourceCondition
{
	public static final MapCodec<TagNotEmptyResourceCondition> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
			.group(Identifier.CODEC.fieldOf("tag").forGetter(TagNotEmptyResourceCondition::tag))
			.apply(builder, TagNotEmptyResourceCondition::new));
	public static final ResourceConditionType<TagNotEmptyResourceCondition> TYPE = ResourceConditionType.create(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "tag_not_empty"), CODEC);

	@Override
	public ResourceConditionType<?> getType()
	{
		return TYPE;
	}

	@Override
	public boolean test(@Nullable RegistryOps.RegistryInfoLookup registryInfo)
	{
		// The entries come from the tags of the reload (LoadedTags): the lookup's named sets of a first load are
		// unbound until the reload applies them (1.21.1 kept them through a mixin into TagManager.LoadResult)
		Boolean populated = LoadedTags.isPopulated(TagKey.create(Registries.ITEM, tag));
		return populated != null && populated;
	}
}
