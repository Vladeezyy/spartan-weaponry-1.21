package com.oblivioussp.spartanweaponry.api.trait;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.core.Holder;


import com.google.common.collect.ImmutableMultimap;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class KnockbackWeaponTrait extends MeleeCallbackWeaponTrait 
{
	public static final ResourceLocation KNOCKBACK_MODIFIER = ResourceLocation.fromNamespaceAndPath(SpartanWeaponryAPI.MOD_ID, "trait.knockback");
	
	public KnockbackWeaponTrait(String type, String modId) 
	{
		super(type, modId, TraitQuality.POSITIVE);
	}
	
	@Override
	public void onModifyAttributesMelee(ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder) 
	{
		builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(KNOCKBACK_MODIFIER, 1.0d, Operation.ADD_VALUE));
	}

}
