package com.oblivioussp.spartanweaponry.mixin;

import com.oblivioussp.spartanweaponry.platform.Platform;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.oblivioussp.spartanweaponry.api.IWeaponTraitContainer;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.oblivioussp.spartanweaponry.api.trait.WeaponTrait;
import com.oblivioussp.spartanweaponry.init.ModDamageTypes;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(Player.class)
public class PlayerMixin 
{
	/**
	 * Sweep II / III traits set the sweep damage ratio. 1.20.1 redirected EnchantmentHelper.getSweepingDamageRatio;
	 * 1.21 reads the SWEEPING_DAMAGE_RATIO attribute in Player.attack (sweep damage = 1 + ratio * damage, as before);
	 * 1.21.11 in Player.doSweepAttack.
	 */
	@WrapOperation(method = "doSweepAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
	private double getSweepingDamageRatio(Player entityIn, Holder<Attribute> attribute, Operation<Double> original)
	{
		if(attribute == Attributes.SWEEPING_DAMAGE_RATIO)
		{
			ItemStack weaponStack = entityIn.getMainHandItem();
			if(weaponStack.getItem() instanceof IWeaponTraitContainer<?> container)
			{
				WeaponTrait sweepTrait = container.getFirstWeaponTraitWithType(WeaponTraits.TYPE_SWEEP_DAMAGE);
				if(sweepTrait != null && sweepTrait.getLevel() > 1)
				{
					float damage = (float)entityIn.getAttributeValue(Attributes.ATTACK_DAMAGE);
					float resultDamage = damage * sweepTrait.getMagnitude();
					return ((resultDamage - 1.0f) / resultDamage) * sweepTrait.getMagnitude();
				}
			}
		}
		return original.call(entityIn, attribute);
	}	
	/** 1.21.11: Player.attack takes its damage source from createAttackSource (the item's own, else playerAttack). */
	@ModifyReturnValue(method = "createAttackSource", at = @At("RETURN"))
	private DamageSource damagePlayerAttack(DamageSource originalSource, ItemStack weaponStack)
	{
		Player playerIn = (Player)(Object)this;
		if(weaponStack.getItem() instanceof IWeaponTraitContainer<?> container)
		{
			WeaponTrait armorPiercingTrait = container.getFirstWeaponTraitWithType(WeaponTraits.TYPE_ARMOUR_PIERCING);
			if(armorPiercingTrait != null)
				return ModDamageTypes.armorPiercingMelee(playerIn);
		}
		// Otherwise, the basic player attack damage source
		return originalSource;
	}
	
/*	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V"))
	private void doPostDamageEffects(LivingEntity livingEntityIn, Entity entityIn)
	{
		Log.debug("Intercepted EnchantmentHelper.doPostDamageEffects(Entity) method!");
		ItemStack weaponStack = livingEntityIn.getMainHandItem();
		if(weaponStack.getItem() instanceof IWeaponTraitContainer<?> container && 
				container.hasWeaponTraitWithType(WeaponTraits.TYPE_QUICK_STRIKE) && entityIn instanceof LivingEntity living)
		{
			living.invulnerableTime = Config.INSTANCE.quickStrikeHurtResistTicks.get();
			Log.debug("Set hurt time to " + Config.INSTANCE.quickStrikeHurtResistTicks.get() + " ticks");
		}
		livingEntityIn.setLastHurtMob(entityIn);
	}*/
}
