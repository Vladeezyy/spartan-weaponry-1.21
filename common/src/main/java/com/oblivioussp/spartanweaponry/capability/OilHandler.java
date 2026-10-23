package com.oblivioussp.spartanweaponry.capability;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import com.oblivioussp.spartanweaponry.util.ItemNbt;

import java.util.Optional;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.api.oil.OilEffect;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

public class OilHandler implements IOilHandler
{
	public static final String NBT_OIL = "Oil";
	public static final String NBT_OIL_EFFECT = "Effect";
	public static final String NBT_USES_LEFT = "UsesLeft";
	
	private final ItemStack stack;
	private Optional<OilEffect> effect;
	private Optional<Holder<Potion>> potion;
	private int usesLeft;
	
	public OilHandler(ItemStack stackIn)
	{
		stack = stackIn;
		effect = Optional.empty();
		potion = Optional.empty();
		usesLeft = 0;
		// Load the values contained in the NBT if available (read only: no copy of the item's NBT)
		CompoundTag oil = oilTag(stack);
		if(oil != null)
			deserializeNBT(oil);
	}
	
	@SuppressWarnings("deprecation")
	private static CompoundTag oilTag(ItemStack stack)
	{
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.tag.contains(NBT_OIL) ? data.tag.getCompoundOrEmpty(NBT_OIL) : null;
	}
	
	/**
	 * {@link #isOiled()} of the stack without making a handler. The oil coating model asks this for every weapon it
	 * renders; going through the capability made a handler and copied the item's NBT twice per weapon and frame.
	 */
	public static boolean isOiled(ItemStack stack)
	{
		CompoundTag oil = oilTag(stack);
		if(oil == null)
			return false;
		Identifier oilId = Identifier.tryParse(oil.getStringOr(NBT_OIL_EFFECT, ""));
		return oilId != null && !oil.getStringOr(NBT_OIL_EFFECT, "").isEmpty() && OilEffects.OIL_REGISTRY.getValue(oilId) != OilEffects.NONE.get();
	}

	@Override
	public Optional<OilEffect> getEffect() 
	{
		return effect;
	}
	
	@Override
	public Optional<Holder<Potion>> getPotion() 
	{
		return potion;
	}

	@Override
	public void setEffect(OilEffect effectIn, ItemStack oilStackIn) 
	{
		effect = Optional.of(effectIn);
		usesLeft = effectIn.getMaxUses();
		potion = effectIn == OilEffects.POTION.get() ? OilHelper.getPotionFromStack(oilStackIn) : Optional.empty();
		ItemNbt.update(stack, (nbt) -> nbt.put(NBT_OIL, serializeNBT()));
	}

	@Override
	public void setPotion(Holder<Potion> potionIn, ItemStack oilStackIn) 
	{
		effect = Optional.of(OilEffects.POTION.get());
		usesLeft = OilEffects.POTION.get().getMaxUses();
		potion = Optional.of(potionIn);
		ItemNbt.update(stack, (nbt) -> nbt.put(NBT_OIL, serializeNBT()));
	}
	
	@Override
	public void clearEffect() 
	{
		effect = Optional.empty();
		usesLeft = 0;
		ItemNbt.update(stack, (nbt) -> nbt.remove(NBT_OIL));
	}
	
	@Override
	public float useEffect(float baseDamageIn, Level levelIn, LivingEntity targetIn, LivingEntity userIn, ItemStack userWeaponIn) 
	{
		OilEffect oilEffect = effect.get();
		ItemStack oilStack = oilEffect == OilEffects.POTION.get() ? OilHelper.makePotionOilStack(potion.get()) : OilHelper.makeOilStack(oilEffect);
		float resultDamage = oilEffect.onUse(baseDamageIn, levelIn, targetIn, userIn, oilStack);
		
		usesLeft--;
		ItemNbt.update(stack, (nbt) -> nbt.put(NBT_OIL, serializeNBT()));
		
		if(usesLeft <= 0)
		{
			if(userIn instanceof Player)
				((Player)userIn).sendOverlayMessage(Component.translatable("message." + ModSpartanWeaponry.ID + ".oil_depleted", oilStack.getHoverName(), userWeaponIn.getHoverName()));
			clearEffect();
		}
		return resultDamage;
	}

	@Override
	public boolean isOiled() 
	{
		return effect.isPresent() && effect.get() != OilEffects.NONE.get();
	}

	@Override
	public int getUsesLeft()
	{
		return usesLeft;
	}

	@Override
	public CompoundTag serializeNBT() 
	{
		CompoundTag nbt = new CompoundTag();
		if(effect.isPresent())
		{
			if(potion.isPresent())
				PotionHelper.putPotion(nbt, potion.get());
			Identifier loc = OilEffects.OIL_REGISTRY.getKey(effect.get());
			nbt.putString(NBT_OIL_EFFECT, loc.toString());
			nbt.putInt(NBT_USES_LEFT, usesLeft);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) 
	{
		String oilEffectReg = nbt.getStringOr(NBT_OIL_EFFECT, "");
		Identifier oilId = oilEffectReg.isEmpty() ? null : Identifier.tryParse(oilEffectReg);
		effect = oilId != null ? Optional.of(OilEffects.OIL_REGISTRY.getValue(oilId)) : Optional.empty();
		potion = PotionHelper.getPotion(nbt);
		usesLeft = nbt.getIntOr(NBT_USES_LEFT, 0);
	}
}
