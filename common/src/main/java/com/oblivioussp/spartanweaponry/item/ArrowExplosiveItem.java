package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.world.item.Item;

import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.entity.projectile.ArrowExplosiveEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ArrowExplosiveItem extends ArrowItemSW 
{
	public ArrowExplosiveItem(float rangeModifier) 
	{
		super();
		this.rangeModifier = rangeModifier;
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon)
	{
		AbstractArrow arrow = new ArrowExplosiveEntity(level, shooter, weapon);
		return arrow;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext level, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn)
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			tooltip.flush();
			super.appendHoverText(stack, level, display, tooltipOut, flagIn);
			tooltip.add(Component.empty());
			tooltip.add(Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".modifiers.projectile.impact.explosion").withStyle(ChatFormatting.BLUE));
		}
		finally
		{
			tooltip.flush();
		}
	}
}