package com.oblivioussp.spartanweaponry.mixin;

import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.Holder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oblivioussp.spartanweaponry.api.tags.ModDamageTypeTags;
import com.oblivioussp.spartanweaponry.util.Config;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends EntityMixin
{
	@Inject(at = @At("HEAD"), method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", cancellable = true)
	protected void getDamageAfterArmorAbsorb(DamageSource source, float damage, CallbackInfoReturnable<Float> callback)
	{
		if(source.is(ModDamageTypeTags.IS_ARMOR_PIERCING) && !source.is(DamageTypeTags.BYPASSES_ARMOR))
		{
			hurtArmor(source, damage);
			float percentage = Config.INSTANCE.armorPiercePercentage.get().floatValue();
//			Log.debug("Found armor piercing damage source! Reducing armor value of target by " + (percentage) + "%");
			float toughness = (float)getAttributeValue(Attributes.ARMOR_TOUGHNESS);
			float armorPiercingDamage = damage * (percentage / 100.0f);			// Damage which ignores armor completely
			float regularDamage = damage - armorPiercingDamage;					// Damage which is absorbed by armor as normal
			float reducedDamage = CombatRules.getDamageAfterAbsorb((LivingEntity)(Object)this, regularDamage, source, (float)getArmorValue(), toughness);
			float resultDamage = armorPiercingDamage + reducedDamage;
//			Log.debug("Full damage: " + damage + " Armor value: " + (float)getArmorValue() + " Damage ignoring armor (" + (percentage) + "% damage): " + armorPiercingDamage + " Damage not ignoring armor: " + regularDamage + " Reduced Damage: " + reducedDamage + " Result Damage: " + resultDamage);
			callback.setReturnValue(resultDamage);
		}
	}
	
	@Shadow
	protected void hurtArmor(DamageSource source, float damage)
	{
		throw new IllegalStateException("Mixin failed to shadow the \"LivingEntity.hurtArmor(float)\" method!");
	}
	
	@Shadow
	public int getArmorValue()
	{
		throw new IllegalStateException("Mixin failed to shadow the \"LivingEntity.getArmorValue()\" method!");
	}
	
	@Shadow
	public double getAttributeValue(Holder<Attribute> attribute)
	{
		throw new IllegalStateException("Mixin failed to shadow the \"LivingEntity.getAttributeValue(Attribute)\" method!");
	}

	@Shadow
	public ItemStack getItemInHand(InteractionHand hand)
	{
		throw new IllegalStateException("Mixin failed to shadow the \"LivingEntity.getItemInHand(InteractionHand)\" method!");
	}
	
	/**
	 * Shield Breach disables a shield as an axe does (5 seconds). 1.21.1: NeoForge IItemExtension#canDisableShield and
	 * a Fabric mixin; 1.21.5+: the weapon component of the attacking item, asked here.
	 */
	@Inject(at = @At("HEAD"), method = "getSecondsToDisableBlocking()F", cancellable = true)
	private void spartanweaponry$shieldBreach(CallbackInfoReturnable<Float> callback)
	{
		LivingEntity self = (LivingEntity)(Object)this;
		ItemStack weapon = self.getWeaponItem();
		if(weapon.getItem() instanceof SwordBaseItem item && weapon == self.getActiveItem() && item.canDisableShield(weapon, ItemStack.EMPTY, null, self))
			callback.setReturnValue(5.0F);
	}

	// 1.21.11: the equipment of every living entity is in LivingEntity (1.21.1: Mob for mobs)
	@Shadow
	public void setItemSlot(EquipmentSlot slotIn, ItemStack item)
	{
		throw new IllegalStateException("Mixin failed to shadow the \"LivingEntity.setItemSlot(...)\" method!");
	}
	
	@Shadow
	public ItemStack getItemBySlot(EquipmentSlot slotIn) 
	{
		throw new IllegalStateException("Mixin failed to shadow the \"LivingEntity.getItemBySlot(...)\" method!");
	}
	
	@Shadow
	public boolean isBaby()
	{
		throw new IllegalStateException("Mixin failed to shadow the \"LivingEntity.isBaby()\" method!");
	}
}
