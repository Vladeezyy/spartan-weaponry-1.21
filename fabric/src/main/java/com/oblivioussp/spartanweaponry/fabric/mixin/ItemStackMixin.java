package com.oblivioussp.spartanweaponry.fabric.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.oblivioussp.spartanweaponry.fabric.FabricItemHooks;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** NeoForge's IItemExtension methods of the weapons that ItemStack asks: durability, attributes, damage. */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	/** The entity of hurtAndBreak(int, LivingEntity, EquipmentSlot), which passes only players on (NeoForge: any). */
	@Unique
	private static final ThreadLocal<LivingEntity> SPARTANWEAPONRY$HURT_BY = new ThreadLocal<>();

	@Shadow
	public abstract boolean isDamageableItem();

	@Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$getMaxDamage(CallbackInfoReturnable<Integer> cir)
	{
		int max = FabricItemHooks.getMaxDamage((ItemStack)(Object)this);
		if(max >= 0)
			cir.setReturnValue(max);
	}

	/** Both forEachModifier methods (slot group: tooltips; slot: the entity's attributes; a name alone selects one of them): IItemExtension#getDefaultAttributeModifiers(ItemStack). */
	@WrapOperation(method = {"forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V", "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"}, require = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getDefaultAttributeModifiers()Lnet/minecraft/world/item/component/ItemAttributeModifiers;"))
	private ItemAttributeModifiers spartanweaponry$attributes(Item item, Operation<ItemAttributeModifiers> original)
	{
		ItemAttributeModifiers modifiers = FabricItemHooks.getDefaultAttributeModifiers((ItemStack)(Object)this);
		return modifiers != null ? modifiers : original.call(item);
	}

	@Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V", at = @At("HEAD"))
	private void spartanweaponry$hurtByStart(int amount, LivingEntity entity, EquipmentSlot slot, CallbackInfo ci)
	{
		SPARTANWEAPONRY$HURT_BY.set(entity);
	}

	@Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V", at = @At("RETURN"))
	private void spartanweaponry$hurtByEnd(int amount, LivingEntity entity, EquipmentSlot slot, CallbackInfo ci)
	{
		SPARTANWEAPONRY$HURT_BY.remove();
	}

	/** IItemExtension#damageItem: the traits change the durability a use costs (before Unbreaking, as on NeoForge). */
	@ModifyVariable(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private int spartanweaponry$damageItem(int damage, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) ServerPlayer player, @Local(argsOnly = true) Consumer<Item> onBreak)
	{
		if(!isDamageableItem())
			return damage;
		LivingEntity entity = SPARTANWEAPONRY$HURT_BY.get();
		return FabricItemHooks.damageItem((ItemStack)(Object)this, damage, entity != null ? entity : player);
	}
}
