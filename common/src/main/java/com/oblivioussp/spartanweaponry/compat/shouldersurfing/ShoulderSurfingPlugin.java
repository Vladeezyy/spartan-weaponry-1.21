package com.oblivioussp.spartanweaponry.compat.shouldersurfing;

import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAimStateEventHandler;
import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;

import net.minecraft.world.entity.LivingEntity;

/**
 * Weapons with a custom crosshair make Shoulder Surfing aim like with a bow (its "adaptive" items).
 * Shoulder Surfing 5.x calls {@link #register(IEventBus)} (5.2 has no registrar of the 4.x versions any more).
 */
public class ShoulderSurfingPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IEventBus eventBus)
	{
		eventBus.register((ComputePlayerAimStateEventHandler)(ev) -> 
		{
			if(!ev.getResult() && isHolding(ev.getEntity()))
				ev.setResult(true);
		});
	}
	
	
	private static boolean isHolding(LivingEntity entity)
	{
		return entity.getMainHandItem().is(ModItemTags.HAS_CUSTOM_CROSSHAIR) || entity.getOffhandItem().is(ModItemTags.HAS_CUSTOM_CROSSHAIR);
	}
}
