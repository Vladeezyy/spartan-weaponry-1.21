package com.oblivioussp.spartanweaponry.port;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** The NeoForge events of the test scenes (dev runs only: port/ is not in the jar). */
@EventBusSubscriber(modid = ModSpartanWeaponry.ID, value = Dist.CLIENT)
public final class NeoForgeSceneHooks
{
	static
	{
		if(ModList.get().isLoaded("curios"))
			DevScene.accessorySlot = (player, quiver) -> top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).flatMap(inv -> inv.getStacksHandler("back")).map(handler -> 
			{
				handler.getStacks().setStackInSlot(0, quiver);
				return true;
			}).orElse(false);
	}

	@SubscribeEvent
	public static void onTick(ClientTickEvent.Post event)
	{
		DevScene.onTick();
		StressScene.onTick();
	}
}
