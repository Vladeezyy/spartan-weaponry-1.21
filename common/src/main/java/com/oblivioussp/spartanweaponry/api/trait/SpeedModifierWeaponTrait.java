package com.oblivioussp.spartanweaponry.api.trait;

import net.minecraft.resources.Identifier;

import net.minecraft.core.Holder;

import java.util.Optional;

import com.google.common.collect.ImmutableMultimap;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.WeaponMaterial;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SpeedModifierWeaponTrait extends WeaponTrait implements IMeleeTraitCallback, IRangedTraitCallback, IThrowingTraitCallback
{
	public static final Identifier SPEED_MODIFIER = Identifier.fromNamespaceAndPath(SpartanWeaponryAPI.MOD_ID, "trait.attack_speed");
	
	public SpeedModifierWeaponTrait(String type, TraitQuality quality)
	{
		super(type, SpartanWeaponryAPI.MOD_ID, quality);
	}

	@Override
	public Optional<IMeleeTraitCallback> getMeleeCallback() 
	{
		return Optional.of(this);
	}
	
	@Override
	public void onModifyAttributesMelee(ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder)
	{
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(SPEED_MODIFIER, getMagnitude(), Operation.ADD_MULTIPLIED_BASE));
	}
	
	@Override
	public Optional<IRangedTraitCallback> getRangedCallback() 
	{
		return Optional.of(this);
	}
	
	@Override
	public float modifyLongbowDrawTime(WeaponMaterial material, float baseDraw) 
	{
		return baseDraw * (1.0f + (getMagnitude() * -1.0f));
	}
	
	@Override
	public int modifyHeavyCrossbowLoadTime(WeaponMaterial material, int baseLoad) 
	{
		return Mth.floor(baseLoad * (1.0f + (getMagnitude() * -1.0f)));
	}
	
	@Override
	public int modifyHeavyCrossbowAimTime(WeaponMaterial material, int baseAim)
	{
		return Mth.floor(baseAim * (1.0f + (getMagnitude() * -1.0f)));
	}
	
	@Override
	public Optional<IThrowingTraitCallback> getThrowingCallback() 
	{
		return Optional.of(this);
	}
	
	@Override
	public int modifyThrowingChargeTime(WeaponMaterial material, int baseCharge)
	{
		return Mth.floor(baseCharge * (1.0f + (getMagnitude() * -1.0f)));
	}
}
