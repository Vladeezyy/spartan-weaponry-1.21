package com.oblivioussp.spartanweaponry.item;

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
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, List<Component> tooltip, TooltipFlag flagIn) 
	{
		super.appendHoverText(stack, levelIn, tooltip, flagIn);

		tooltip.add(Component.empty());
		PotionHelper.addPotionTooltip(stack, tooltip, 0.125f, levelIn.tickRate());
	}

	public Component getName(ItemStack stack)
	{
		String potionKey = Potion.getName(PotionHelper.getPotion(stack), "item.spartanweaponry.proj_tipped.effect.");
		return Component.translatable(potionKey, Component.translatable("item." + ModSpartanWeaponry.ID + "." + baseName));
	}
}
