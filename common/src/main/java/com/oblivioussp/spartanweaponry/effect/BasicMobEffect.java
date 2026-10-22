package com.oblivioussp.spartanweaponry.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.registries.BuiltInRegistries;

public class BasicMobEffect extends MobEffect 
{
	public BasicMobEffect(MobEffectCategory effectCategoryIn, int colourIn) 
	{
		super(effectCategoryIn, colourIn);
	}
	
	@Override
	protected String getOrCreateDescriptionId() 
	{
		if(descriptionId == null)
		{
			Identifier loc = BuiltInRegistries.MOB_EFFECT.getKey(this);
			descriptionId = "effect." + loc.getNamespace() + "." + loc.getPath();
		}
		return descriptionId;
	}
}
