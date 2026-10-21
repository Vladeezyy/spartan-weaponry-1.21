package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import net.minecraft.core.registries.Registries;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.block.entity.ExtendedSkullBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModBlockEntities 
{
	public static final Registrar<BlockEntityType<?>> REGISTRY = Registrar.create(Registries.BLOCK_ENTITY_TYPE);
	
	public static final Registrar.Entry<BlockEntityType<?>, BlockEntityType<ExtendedSkullBlockEntity>> EXTENDED_SKULL_TYPE = REGISTRY.register("skull_extended", () -> BlockEntityType.Builder.of(ExtendedSkullBlockEntity::new, 
			ModBlocks.BLAZE_HEAD.get(), ModBlocks.BLAZE_WALL_HEAD.get(),
			ModBlocks.ENDERMAN_HEAD.get(), ModBlocks.ENDERMAN_WALL_HEAD.get(),
			ModBlocks.SPIDER_HEAD.get(), ModBlocks.SPIDER_WALL_HEAD.get(),
			ModBlocks.CAVE_SPIDER_HEAD.get(), ModBlocks.CAVE_SPIDER_WALL_HEAD.get(),
			ModBlocks.ZOMBIFIED_PIGLIN_HEAD.get(), ModBlocks.ZOMBIFIED_PIGLIN_WALL_HEAD.get(),
			ModBlocks.HUSK_HEAD.get(), ModBlocks.HUSK_WALL_HEAD.get(),
			ModBlocks.STRAY_SKULL.get(), ModBlocks.STRAY_WALL_SKULL.get(),
			ModBlocks.DROWNED_HEAD.get(), ModBlocks.DROWNED_WALL_HEAD.get(),
			ModBlocks.ILLAGER_HEAD.get(), ModBlocks.ILLAGER_WALL_HEAD.get(),
			ModBlocks.WITCH_HEAD.get(), ModBlocks.WITCH_WALL_HEAD.get()).build(null));
}
