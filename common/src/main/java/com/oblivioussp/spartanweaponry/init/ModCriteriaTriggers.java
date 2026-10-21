package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.advancement.criterion.BrewOilTrigger;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;

public class ModCriteriaTriggers 
{
	public static final Registrar<CriterionTrigger<?>> REGISTRY = Registrar.create(Registries.TRIGGER_TYPE);
	
	public static final Registrar.Entry<CriterionTrigger<?>, BrewOilTrigger> BREW_OIL = REGISTRY.register("brew_oil", BrewOilTrigger::new);
}
