package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.effect.BasicMobEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModMobEffects 
{
	public static Registrar<MobEffect> REGISTRY = Registrar.create(Registries.MOB_EFFECT);
	public static Registrar.Entry<MobEffect, MobEffect> ENDER_DISRPUTION = REGISTRY.register("ender_disruption", () -> new BasicMobEffect(MobEffectCategory.HARMFUL, 0x408080));
}
