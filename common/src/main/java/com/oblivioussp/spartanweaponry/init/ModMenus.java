package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.platform.Platform;
import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import net.minecraft.core.registries.Registries;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.inventory.QuiverArrowMenu;
import com.oblivioussp.spartanweaponry.inventory.QuiverBoltMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModMenus 
{
	public static final Registrar<MenuType<?>> REGISTRY = Registrar.create(Registries.MENU);
	
	public static final Registrar.Entry<MenuType<?>, MenuType<QuiverArrowMenu>> QUIVER_ARROW = REGISTRY.register("quiver_arrow", () -> Platform.INSTANCE.createMenuType(QuiverArrowMenu::createFromNetwork));
	public static final Registrar.Entry<MenuType<?>, MenuType<QuiverBoltMenu>> QUIVER_BOLT = REGISTRY.register("quiver_bolt", () -> Platform.INSTANCE.createMenuType(QuiverBoltMenu::createFromNetwork));
}
