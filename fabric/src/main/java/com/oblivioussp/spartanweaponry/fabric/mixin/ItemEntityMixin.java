package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.oblivioussp.spartanweaponry.event.CommonEventHandler;
import com.oblivioussp.spartanweaponry.event.LoaderEvents;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/** NeoForge ItemEntityPickupEvent.Pre: thrown weapons and arrows go into the quiver / the stack they came from. */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
	@Inject(method = "playerTouch", at = @At("HEAD"))
	private void spartanweaponry$pickup(Player player, CallbackInfo ci)
	{
		ItemEntity self = (ItemEntity)(Object)this;
		if(!self.level().isClientSide())
			CommonEventHandler.onPlayerPickup(new LoaderEvents.ItemPickup(player, self));
	}
}
