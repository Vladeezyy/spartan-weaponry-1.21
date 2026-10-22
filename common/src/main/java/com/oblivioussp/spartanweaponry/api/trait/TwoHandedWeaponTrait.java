package com.oblivioussp.spartanweaponry.api.trait;

import java.util.List;

import com.oblivioussp.spartanweaponry.api.WeaponMaterial;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TwoHandedWeaponTrait extends MeleeCallbackWeaponTrait
{
	public TwoHandedWeaponTrait(String typeIn, String modIdIn) 
	{
		super(typeIn, modIdIn, TraitQuality.NEGATIVE);
		isMelee = true;
	}
	
	@Override
	protected void addTooltipDescription(ItemStack stack, List<Component> tooltip)
	{
		tooltip.add(tooltipIndent().append(Component.translatable(String.format("tooltip.%s.trait.%s.desc", modId, this.type), magnitude * 100.0f).withStyle(WeaponTrait.DESCRIPTION_FORMAT)));
	}

	@Override
	public void onItemUpdate(WeaponMaterial material, ItemStack stack, Level level, LivingEntity entity, int itemSlot, boolean isSelected)
	{
		ItemStack mainHand = entity.getMainHandItem();
		ItemStack offHand = entity.getOffhandItem();
		MobEffectInstance effect = entity.getEffect(MobEffects.MINING_FATIGUE);
		
		// If the weapon is equipped in the main-hand and anything else is equipped in the off-hand, give mining fatigue
		if((isSelected && ItemStack.isSameItem(stack, mainHand) && !offHand.isEmpty()) ||
				(ItemStack.isSameItem(stack, offHand) && !mainHand.isEmpty()))
		{
			// Apply Mining Fatigue as often as needed.
			if(effect == null || effect.endsWithin(1))
				entity.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 20, this.level == 2 ? 3 : 2, false, false));
		}
		// Port fix (issue #169): 1.20.1 removed Mining Fatigue here when its duration was <= 0, which is any effect of
		// infinite duration (-1), whatever gave it; the 20 tick effect given above simply runs out
	}
	
	@Override
	public float modifyDamageDealt(WeaponMaterial material, float baseDamage, DamageSource source, LivingEntity attacker, LivingEntity victim) 
	{
		float resultDamage = baseDamage;
		ItemStack mainHand = attacker.getMainHandItem();
		ItemStack offHand = attacker.getOffhandItem();
		
		if(!mainHand.isEmpty() && !offHand.isEmpty())
		{
			resultDamage *= (1.0f - magnitude);
		}
		return resultDamage;
	}
}
