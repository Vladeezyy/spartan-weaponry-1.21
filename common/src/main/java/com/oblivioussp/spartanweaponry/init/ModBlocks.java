package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import net.minecraft.core.registries.Registries;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.block.ExtendedSkullBlock;
import com.oblivioussp.spartanweaponry.block.ExtendedWallSkullBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModBlocks
{
	public static final Registrar<Block> REGISTRY = Registrar.create(Registries.BLOCK);
	
	public static final Registrar.Entry<Block, Block> BLAZE_HEAD = REGISTRY.register("blaze_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.BLAZE, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> BLAZE_WALL_HEAD = REGISTRY.register("blaze_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.BLAZE, Block.Properties.of().strength(1.0f).dropsLike(BLAZE_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> ENDERMAN_HEAD = REGISTRY.register("enderman_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.ENDERMAN, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> ENDERMAN_WALL_HEAD = REGISTRY.register("enderman_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.ENDERMAN, Block.Properties.of().strength(1.0f).dropsLike(ENDERMAN_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> SPIDER_HEAD = REGISTRY.register("spider_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.SPIDER, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> SPIDER_WALL_HEAD = REGISTRY.register("spider_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.SPIDER, Block.Properties.of().strength(1.0f).dropsLike(SPIDER_HEAD.get()).pushReaction(PushReaction.DESTROY)));	
	public static final Registrar.Entry<Block, Block> CAVE_SPIDER_HEAD = REGISTRY.register("cave_spider_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.CAVE_SPIDER, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> CAVE_SPIDER_WALL_HEAD = REGISTRY.register("cave_spider_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.CAVE_SPIDER, Block.Properties.of().strength(1.0f).dropsLike(CAVE_SPIDER_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> ZOMBIFIED_PIGLIN_HEAD = REGISTRY.register("zombified_piglin_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.ZOMBIE_PIGLIN, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> ZOMBIFIED_PIGLIN_WALL_HEAD = REGISTRY.register("zombified_piglin_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.ZOMBIE_PIGLIN, Block.Properties.of().strength(1.0f).dropsLike(ZOMBIFIED_PIGLIN_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> HUSK_HEAD = REGISTRY.register("husk_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.HUSK, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> HUSK_WALL_HEAD = REGISTRY.register("husk_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.HUSK, Block.Properties.of().strength(1.0f).dropsLike(HUSK_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> STRAY_SKULL = REGISTRY.register("stray_skull", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.STRAY, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> STRAY_WALL_SKULL = REGISTRY.register("stray_wall_skull", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.STRAY, Block.Properties.of().strength(1.0f).dropsLike(STRAY_SKULL.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> DROWNED_HEAD = REGISTRY.register("drowned_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.DROWNED, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> DROWNED_WALL_HEAD = REGISTRY.register("drowned_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.DROWNED, Block.Properties.of().strength(1.0f).dropsLike(DROWNED_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> ILLAGER_HEAD = REGISTRY.register("illager_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.ILLAGER, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> ILLAGER_WALL_HEAD = REGISTRY.register("illager_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.ILLAGER, Block.Properties.of().strength(1.0f).dropsLike(ILLAGER_HEAD.get()).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> WITCH_HEAD = REGISTRY.register("witch_head", () -> new ExtendedSkullBlock(ExtendedSkullBlock.Types.WITCH, Block.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY)));
	public static final Registrar.Entry<Block, Block> WITCH_WALL_HEAD = REGISTRY.register("witch_wall_head", () -> new ExtendedWallSkullBlock(ExtendedSkullBlock.Types.WITCH, Block.Properties.of().strength(1.0f).dropsLike(WITCH_HEAD.get()).pushReaction(PushReaction.DESTROY)));
}
