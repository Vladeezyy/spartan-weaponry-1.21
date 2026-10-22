package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.entity.projectile.BoltEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import com.oblivioussp.spartanweaponry.util.PotionHelper;
import net.minecraft.world.level.Level;

public class BoltTippedItem extends BoltItem
{
	protected String baseName;
	
	public BoltTippedItem(String baseName, float damageModifier, float rangeModifier, float armorPiercingFactor) 
	{
		super(damageModifier, rangeModifier, armorPiercingFactor);
		this.baseName = baseName;
	}
	
	@Override
	public BoltEntity createBolt(Level level, ItemStack stack, LivingEntity shooter) 
	{
		BoltEntity bolt = new BoltEntity(shooter, level);
    	ItemStack boltStack = stack.copy();
    	boltStack.setCount(1);
    	bolt.initEntity(damageModifier, rangeModifier, armorPiercingFactor, boltStack);
		bolt.setPotionEffect(stack);
    	if(bolt.isValid())
    		return bolt;
    	
    	return null;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn) 
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			tooltip.flush();
			super.appendHoverText(stack, levelIn, display, tooltipOut, flagIn);

			tooltip.add(Component.empty());
			PotionHelper.addPotionTooltip(stack, tooltip, 0.125f, levelIn.tickRate());
		}
		finally
		{
			tooltip.flush();
		}
	}

	public Component getName(ItemStack stack)
	{
		String potionKey = PotionHelper.getName(PotionHelper.getPotion(stack), "item.spartanweaponry.proj_tipped.effect.");
		return Component.translatable(potionKey, Component.translatable("item." + ModSpartanWeaponry.ID + "." + baseName));
	}
}
