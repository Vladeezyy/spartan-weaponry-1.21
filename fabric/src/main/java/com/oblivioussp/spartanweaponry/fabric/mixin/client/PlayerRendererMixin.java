package com.oblivioussp.spartanweaponry.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** NeoForge: the crossbow poses for any CrossbowItem (the heavy crossbows; vanilla: Items.CROSSBOW only). */
// 1.21.9+: PlayerRenderer is AvatarRenderer
@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin
{
	@WrapOperation(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private static boolean spartanweaponry$crossbow(ItemStack stack, Item item, Operation<Boolean> original)
	{
		if(item == Items.CROSSBOW)
			return stack.getItem() instanceof CrossbowItem;
		return original.call(stack, item);
	}
}
