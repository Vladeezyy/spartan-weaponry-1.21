package com.oblivioussp.spartanweaponry.mixin;

import net.minecraft.core.registries.Registries;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.oblivioussp.spartanweaponry.util.ItemRandomizer;

import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(Mob.class)
public class MobMixin extends LivingEntityMixin
{
	@Shadow
	@Final
	public GoalSelector goalSelector;
	
	
	
	protected void attemptReplacingMainHandItemRandom(@NotNull TagKey<Item> itemTagIn, DifficultyInstance difficultyIn, boolean disabledIn, float chanceNormalIn, float chanceHardIn)
	{
		if(!disabledIn)
		{
			float rand = random.nextFloat();
			float chance = difficultyIn.isHard() ? chanceHardIn : chanceNormalIn;
			
			if(rand > 1 - chance)
			{
				Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.get(itemTagIn);
				if(tag.isPresent() && tag.get().size() > 0)
				{
					ItemStack weapon = ItemStack.EMPTY;
					List<Item> possibleWeapons = tag.get().stream().map(Holder::value).toList();
					weapon = ItemRandomizer.generate(random, possibleWeapons);
					setItemSlot(EquipmentSlot.MAINHAND, weapon);
				}
			}
		}
	}
}
