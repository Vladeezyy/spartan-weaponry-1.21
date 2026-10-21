package com.oblivioussp.spartanweaponry.command;

import java.util.function.Predicate;

import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;

public class PotionInput implements Predicate<Holder<Potion>> 
{
	private final Holder<Potion> potion;
	
	public PotionInput(Holder<Potion> potionIn)
	{
		potion = potionIn;
	}
	
	public Holder<Potion> getEffect()
	{
		return potion;
	}
	
	@Override
	public boolean test(Holder<Potion> t) 
	{
		return potion == t;
	}
}
