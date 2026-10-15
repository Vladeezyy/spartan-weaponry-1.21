package com.oblivioussp.spartanweaponry.api.oil;

import java.util.List;
import java.util.Optional;

import com.oblivioussp.spartanweaponry.capability.OilHandler;
import com.oblivioussp.spartanweaponry.util.Config;
import com.oblivioussp.spartanweaponry.util.Defaults;
import com.oblivioussp.spartanweaponry.util.ItemNbt;
import com.oblivioussp.spartanweaponry.util.OilHelper;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class PotionOilEffect extends OilEffect 
{
	public PotionOilEffect()
	{
		super("potion", OilEffectType.EFFECT_ONLY, 0x0, Defaults.OIL_USES_NORMAL, 0.0f, OilEffect.USE_NOTHING, true);
	}
	
	@Override
	public int getColor(ItemStack stackIn) 
	{
		CompoundTag oilTag = ItemNbt.get(stackIn).getCompound(OilHandler.NBT_OIL);
		Optional<Holder<Potion>> potion = PotionHelper.getPotion(oilTag);
		if(potion.isPresent())
			return PotionContents.getColor(potion.get());
		return super.getColor(stackIn);
	}

	@Override
	public float onUse(float baseDamageIn, Level levelIn, LivingEntity targetEntityIn, LivingEntity userEntityIn, ItemStack oilStackIn) 
	{
		CompoundTag oilTag = ItemNbt.get(oilStackIn).getCompound(OilHandler.NBT_OIL);
		Optional<Holder<Potion>> potion = PotionHelper.getPotion(oilTag);
		if(potion.isPresent())
		{
			potion.get().value().getEffects().forEach((effect) -> {
				if(effect.getEffect().value().isInstantenous())
				{
					// Temporarily bypass hurt time
					int targetHurtTime = targetEntityIn.hurtTime;
					targetEntityIn.hurtTime = 0;
					effect.getEffect().value().applyInstantenousEffect(userEntityIn, userEntityIn, targetEntityIn, effect.getAmplifier(), 1.0d);
					// Restore hurt time
					targetEntityIn.hurtTime = targetHurtTime;
				}
				else
					targetEntityIn.addEffect(new MobEffectInstance(effect.getEffect(), Mth.floor(effect.getDuration() * Config.INSTANCE.potionOilDurationModifier.get()), effect.getAmplifier()), userEntityIn);
			});
		}
		return super.onUse(baseDamageIn, levelIn, targetEntityIn, userEntityIn, oilStackIn);
	}
	
	@Override
	public void getTooltip(ItemStack stackIn, List<Component> tooltipListIn)
	{
		OilHelper.addPotionTooltip(stackIn, tooltipListIn, Config.INSTANCE.potionOilDurationModifier.get().floatValue());
	}
}
