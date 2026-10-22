package com.oblivioussp.spartanweaponry.compat.jei;

import com.oblivioussp.spartanweaponry.util.PotionHelper;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class TippedProjectileSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> 
{
	public static final TippedProjectileSubtypeInterpreter INSTANCE = new TippedProjectileSubtypeInterpreter();
	
	private TippedProjectileSubtypeInterpreter() {}

	@Override
	public String apply(ItemStack itemStack, UidContext context) 
	{
		PotionContents contents = PotionHelper.getContents(itemStack);
		if (contents.potion().isEmpty() && !contents.hasEffects())
			return IIngredientSubtypeInterpreter.NONE;
		
		String potionTypeString = PotionHelper.getName(contents.potion(), "");
		StringBuilder stringBuilder = new StringBuilder(potionTypeString);
		for (MobEffectInstance effect : contents.getAllEffects()) 
		{
			stringBuilder.append(";").append(effect);
		}
		return stringBuilder.toString();
	}
}
