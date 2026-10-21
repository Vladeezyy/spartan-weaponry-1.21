package com.oblivioussp.spartanweaponry.api.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags
{
	public static final TagKey<Block> GRASS = TagKey.create(Registries.BLOCK, ResourceLocation.parse("forge:grass"));
}
