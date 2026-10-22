package com.oblivioussp.spartanweaponry.compat.jei;

import java.util.Optional;

import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.api.oil.OilEffect;
import com.oblivioussp.spartanweaponry.capability.OilHandler;
import com.oblivioussp.spartanweaponry.util.ItemNbt;
import com.oblivioussp.spartanweaponry.util.OilHelper;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

public class WeaponOilSubtypeInterpreter implements ISubtypeInterpreter<ItemStack>
{
	public static final WeaponOilSubtypeInterpreter INSTANCE = new WeaponOilSubtypeInterpreter();
	
	private WeaponOilSubtypeInterpreter() {}
	
	@Override
	public Object getSubtypeData(ItemStack itemStack, UidContext context)
	{
		if (!ItemNbt.has(itemStack))
			return null;
		
		OilEffect weaponOil = OilHelper.getOilFromStack(itemStack);
		Optional<Holder<Potion>> potion = OilHelper.getPotionFromStack(itemStack);
		String result = OilEffects.OIL_REGISTRY.getKey(weaponOil).getPath();
		if(weaponOil == OilEffects.POTION.get())
		{
			StringBuilder stringBuilder = new StringBuilder(result);
			stringBuilder.append(PotionHelper.getName(potion, ":"));
			for(MobEffectInstance mobEffect : PotionHelper.getEffects(ItemNbt.read(itemStack).getCompoundOrEmpty(OilHandler.NBT_OIL)))
			{
				stringBuilder.append(";").append(mobEffect);
			}
			result = stringBuilder.toString();
		}
		return result;
	}
}
