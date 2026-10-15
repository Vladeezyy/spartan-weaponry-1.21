package com.oblivioussp.spartanweaponry.init;

import net.minecraft.core.registries.Registries;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.block.ExtendedSkullBlock;
import com.oblivioussp.spartanweaponry.block.ExtendedWallSkullBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlocks
{
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(ModSpartanWeaponry.ID);
	
	public static final DeferredBlock<Block> BLAZE_HEAD = REGISTRY.register("blaze_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.BLAZE, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> BLAZE_WALL_HEAD = REGISTRY.register("blaze_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.BLAZE, Block.Properties.of().strength(1.0f).lootFrom(() -> BLAZE_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> ENDERMAN_HEAD = REGISTRY.register("enderman_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.ENDERMAN, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> ENDERMAN_WALL_HEAD = REGISTRY.register("enderman_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.ENDERMAN, Block.Properties.of().strength(1.0f).lootFrom(() -> ENDERMAN_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> SPIDER_HEAD = REGISTRY.register("spider_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.SPIDER, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> SPIDER_WALL_HEAD = REGISTRY.register("spider_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.SPIDER, Block.Properties.of().strength(1.0f).lootFrom(() -> SPIDER_HEAD.get()).pushReaction(PushReaction.DESTROY)));	
	public static final DeferredBlock<Block> CAVE_SPIDER_HEAD = REGISTRY.register("cave_spider_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.CAVE_SPIDER, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> CAVE_SPIDER_WALL_HEAD = REGISTRY.register("cave_spider_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.CAVE_SPIDER, Block.Properties.of().strength(1.0f).lootFrom(() -> CAVE_SPIDER_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> ZOMBIFIED_PIGLIN_HEAD = REGISTRY.register("zombified_piglin_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.ZOMBIE_PIGLIN, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> ZOMBIFIED_PIGLIN_WALL_HEAD = REGISTRY.register("zombified_piglin_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.ZOMBIE_PIGLIN, Block.Properties.of().strength(1.0f).lootFrom(() -> ZOMBIFIED_PIGLIN_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> HUSK_HEAD = REGISTRY.register("husk_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.HUSK, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> HUSK_WALL_HEAD = REGISTRY.register("husk_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.HUSK, Block.Properties.of().strength(1.0f).lootFrom(() -> HUSK_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> STRAY_SKULL = REGISTRY.register("stray_skull", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.STRAY, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> STRAY_WALL_SKULL = REGISTRY.register("stray_wall_skull", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.STRAY, Block.Properties.of().strength(1.0f).lootFrom(() -> STRAY_SKULL.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> DROWNED_HEAD = REGISTRY.register("drowned_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.DROWNED, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> DROWNED_WALL_HEAD = REGISTRY.register("drowned_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.DROWNED, Block.Properties.of().strength(1.0f).lootFrom(() -> DROWNED_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> ILLAGER_HEAD = REGISTRY.register("illager_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.ILLAGER, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> ILLAGER_WALL_HEAD = REGISTRY.register("illager_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.ILLAGER, Block.Properties.of().strength(1.0f).lootFrom(() -> ILLAGER_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> WITCH_HEAD = REGISTRY.register("witch_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.WITCH, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final DeferredBlock<Block> WITCH_WALL_HEAD = REGISTRY.register("witch_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.WITCH, Block.Properties.of().strength(1.0f).lootFrom(() -> WITCH_HEAD.get()).pushReaction(PushReaction.DESTROY)));
}
