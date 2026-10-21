package com.oblivioussp.spartanweaponry.block.entity;

import com.oblivioussp.spartanweaponry.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExtendedSkullBlockEntity extends SkullBlockEntity 
{
	public ExtendedSkullBlockEntity(BlockPos blockPos, BlockState blockState) 
	{
		super(blockPos, blockState);
	}
	
	@Override
	public BlockEntityType<?> getType()
	{
		return ModBlockEntities.EXTENDED_SKULL_TYPE.get();
	}
	
	// Vanilla checks the block against its type field (the vanilla skull type); NeoForge already asks getType()
	@Override
	public boolean isValidBlockState(BlockState blockState)
	{
		return getType().isValid(blockState);
	}
}
