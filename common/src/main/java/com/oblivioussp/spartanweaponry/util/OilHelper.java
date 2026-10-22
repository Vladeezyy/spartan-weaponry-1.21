package com.oblivioussp.spartanweaponry.util;

import java.util.List;
import java.util.Optional;

import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.api.oil.OilEffect;
import com.oblivioussp.spartanweaponry.capability.OilHandler;
import com.oblivioussp.spartanweaponry.init.ModItems;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class OilHelper
{
	public static OilEffect getOilFromStack(ItemStack stackIn)
	{
		CompoundTag tag = ItemNbt.read(stackIn).getCompoundOrEmpty(OilHandler.NBT_OIL);
		Identifier oil = Identifier.tryParse(tag.getStringOr(OilHandler.NBT_OIL_EFFECT, ""));
		if(oil != null && OilEffects.OIL_REGISTRY.containsKey(oil))
			return OilEffects.OIL_REGISTRY.getValue(oil);
		return OilEffects.NONE.get();
	}
	
	public static ItemStack makeOilStack(OilEffect oilIn)
	{
		ItemStack stack = new ItemStack(ModItems.WEAPON_OIL.get());
		CompoundTag tag = new CompoundTag();
		tag.putString(OilHandler.NBT_OIL_EFFECT, OilEffects.OIL_REGISTRY.getKey(oilIn).toString());
		ItemNbt.update(stack, (nbt) -> nbt.put(OilHandler.NBT_OIL, tag));
		return stack;
	}
	
	public static Optional<Holder<Potion>> getPotionFromStack(ItemStack stackIn)
	{
		return PotionHelper.getPotion(ItemNbt.read(stackIn).getCompoundOrEmpty(OilHandler.NBT_OIL));
	}
	
	public static ItemStack makePotionOilStack(Holder<Potion> potionIn)
	{
		ItemStack stack = makeOilStack(OilEffects.POTION.get());
		ItemNbt.update(stack, (nbt) -> PotionHelper.putPotion(nbt.getCompoundOrEmpty(OilHandler.NBT_OIL), potionIn));
		return stack;
	}
	
	public static boolean isValidPotion(Holder<Potion> potionIn)
	{
		boolean isValidPotion = true;
		String id = PotionHelper.getId(potionIn);
		if(potionIn.value().getEffects().isEmpty() || Config.INSTANCE.potionOilBlacklist.get().contains(id))
			return false;
		
		if(Config.INSTANCE.potionOilWhitelist.get().contains(id))
			return true;
			
		for(MobEffectInstance effect : potionIn.value().getEffects())
		{
			// Block non-harmful effects
			if(effect.getEffect().value().getCategory() != MobEffectCategory.HARMFUL)
			{
				isValidPotion = false;
				break;
			}
		}
		return isValidPotion;
	}
	
	public static void addPotionTooltip(ItemStack stackIn, List<Component> tooltipListIn, float durationModifierIn)
	{
		addPotionTooltip(ItemNbt.read(stackIn).getCompoundOrEmpty(OilHandler.NBT_OIL), tooltipListIn, durationModifierIn);
	}
	
	/** The potion tooltip of 1.20.1 (effects, levels, durations, "When Applied" attributes) for the potion in an oil tag */
	public static void addPotionTooltip(CompoundTag tagIn, List<Component> tooltipListIn, float durationModifierIn)
	{
		PotionContents.addPotionTooltip(PotionHelper.getEffects(tagIn), tooltipListIn::add, durationModifierIn, 20.0f);
	}
}
