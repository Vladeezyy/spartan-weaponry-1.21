package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.api.ItemIds;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.entity.projectile.ArrowBaseEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.ArrowExplosiveEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.BoltEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.BoltSpectralEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.BoomerangEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.DynamiteEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.JavelinEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.ThrowingKnifeEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.ThrowingWeaponEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.TomahawkEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModEntities 
{
	public static final Registrar<EntityType<?>> REGISTRY = Registrar.create(Registries.ENTITY_TYPE);
	
	public static final Registrar.Entry<EntityType<?>, EntityType<ArrowBaseEntity>> ARROW_SW = REGISTRY.register("arrow", () -> EntityType.Builder.<ArrowBaseEntity>of(ArrowBaseEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<ArrowExplosiveEntity>> ARROW_EXPLOSIVE = REGISTRY.register("explosive_arrow", () -> EntityType.Builder.<ArrowExplosiveEntity>of(ArrowExplosiveEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<BoltEntity>> BOLT = REGISTRY.register("bolt", () -> EntityType.Builder.<BoltEntity>of(BoltEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<BoltSpectralEntity>> BOLT_SPECTRAL = REGISTRY.register("spectral_bolt", () -> EntityType.Builder.<BoltSpectralEntity>of(BoltSpectralEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<ThrowingWeaponEntity>> THROWING_WEAPON = REGISTRY.register("throwing_weapon", () -> EntityType.Builder.<ThrowingWeaponEntity>of(ThrowingWeaponEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<ThrowingKnifeEntity>> THROWING_KNIFE = REGISTRY.register("throwing_knife", () -> EntityType.Builder.<ThrowingKnifeEntity>of(ThrowingKnifeEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<TomahawkEntity>> TOMAHAWK = REGISTRY.register("tomahawk", () -> EntityType.Builder.<TomahawkEntity>of(TomahawkEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<JavelinEntity>> JAVELIN = REGISTRY.register("javelin", () -> EntityType.Builder.<JavelinEntity>of(JavelinEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<BoomerangEntity>> BOOMERANG = REGISTRY.register("boomerang", () -> EntityType.Builder.<BoomerangEntity>of(BoomerangEntity::new, MobCategory.MISC).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
	public static final Registrar.Entry<EntityType<?>, EntityType<DynamiteEntity>> DYNAMITE = REGISTRY.register("dynamite", () -> EntityType.Builder.<DynamiteEntity>of(DynamiteEntity::new, MobCategory.MISC).
			clientTrackingRange(4).
			updateInterval(20).
			sized(0.5f, 0.5f).
			build(ItemIds.key()));
}
