package com.oblivioussp.spartanweaponry.init;

import net.minecraft.resources.Identifier;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import com.oblivioussp.spartanweaponry.client.model.OilCoatedItemModel;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = ModSpartanWeaponry.ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ModModelLoaders
{
	@SubscribeEvent
	public static void register(ModelEvent.RegisterGeometryLoaders ev)
	{
		ev.register(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil_coated_item"), OilCoatedItemModel.Loader.INSTANCE);
	}
}

/*import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.neoforged.bus.api.IEventBus;


public class ModModelLoaders 
{
	public static void register()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.<ModelRegistryEvent>addListener(ev -> ModelLoaderRegistry.registerLoader(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil_coated_item"), OilCoatedItemModel.Loader.INSTANCE));
	}
}*/
