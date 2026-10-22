package com.oblivioussp.spartanweaponry.api.trait;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;

import net.minecraft.resources.Identifier;

import net.minecraft.core.Holder;


import com.google.common.collect.ImmutableMultimap;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ReachWeaponTrait extends MeleeCallbackWeaponTrait
{
	public static final Identifier ATTACK_REACH_MODIFIER = Identifier.fromNamespaceAndPath(SpartanWeaponryAPI.MOD_ID, "trait.reach");
	
	public ReachWeaponTrait(String propType, String propModId) 
	{
		super(propType, propModId, TraitQuality.POSITIVE);
	}

	@Override
	public void onModifyAttributesMelee(ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder) 
	{
		builder.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ATTACK_REACH_MODIFIER, getMagnitude() - 5.0, AttributeModifier.Operation.ADD_VALUE));
	}
}
