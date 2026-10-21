package com.oblivioussp.spartanweaponry.item;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;

import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.entity.projectile.ArrowBaseEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import com.oblivioussp.spartanweaponry.util.PotionHelper;
import net.minecraft.world.level.Level;

public class ArrowBaseTippedItem extends ArrowBaseItem
{
	protected String baseName;
	
	public ArrowBaseTippedItem(String baseName, float damageModifier, float rangeModifier) 
	{
		super(damageModifier, rangeModifier);
		this.baseName = baseName;
	}
	
	@Override
	public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon) 
	{
		ArrowBaseEntity arrow = new ArrowBaseEntity(level, shooter, weapon);
		ItemStack arrowStack = stack.copy();
		arrowStack.setCount(1);
		arrow.initEntity(damageModifier, rangeModifier, arrowStack);
		arrow.setPotionEffect(stack);
		
		if(arrow.isValid())
			return arrow;
		return null;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, List<Component> tooltip, TooltipFlag flagIn) 
	{
		super.appendHoverText(stack, levelIn, tooltip, flagIn);
		tooltip.add(Component.empty());
		PotionHelper.addPotionTooltip(stack, tooltip, 0.125f, levelIn.tickRate());
	}

	@Override
	public Component getName(ItemStack stack)
	{
		String potionKey = Potion.getName(PotionHelper.getPotion(stack), "item.spartanweaponry.proj_tipped.effect.");
		return Component.translatable(potionKey, Component.translatable("item." + ModSpartanWeaponry.ID + "." + baseName));
	}
}
