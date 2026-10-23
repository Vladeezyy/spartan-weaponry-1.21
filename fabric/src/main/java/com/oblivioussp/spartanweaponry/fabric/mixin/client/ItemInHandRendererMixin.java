package com.oblivioussp.spartanweaponry.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** NeoForge: the crossbow poses for any CrossbowItem (the heavy crossbows; vanilla: Items.CROSSBOW only). */
// 26.3: ItemInHandRenderer is FirstPersonHandsAndItemsRenderer
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class ItemInHandRendererMixin
{
	@WrapOperation(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
	// 26.1: is(Item) is TypedInstance#is(T), erased to Object
	private boolean spartanweaponry$crossbow(ItemStack stack, Object item, Operation<Boolean> original)
	{
		if(item == Items.CROSSBOW)
			return stack.getItem() instanceof CrossbowItem;
		return original.call(stack, item);
	}
}
