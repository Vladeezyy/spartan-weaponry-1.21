package com.oblivioussp.spartanweaponry.util;

import com.oblivioussp.spartanweaponry.api.ItemIds;
import com.oblivioussp.spartanweaponry.api.IInternalMethodHandler;
import com.oblivioussp.spartanweaponry.api.WeaponMaterial;

import net.minecraft.world.item.Item;

public class InternalAPIMethodHandler implements IInternalMethodHandler
{
	//---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
	// Weapon Creation functions
	//---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----

	@Override
	public Item addDagger(WeaponMaterial material)
	{
		return WeaponFactory.DAGGER.create(material, ItemIds.properties());
	}
	
	@Override
	public Item addParryingDagger(WeaponMaterial material) 
	{
		return WeaponFactory.PARRYING_DAGGER.create(material, ItemIds.properties());
	}

	@Override
	public Item addLongsword(WeaponMaterial material)
	{
		return WeaponFactory.LONGSWORD.create(material, ItemIds.properties());
	}

	@Override
	public Item addKatana(WeaponMaterial material) 
	{
		return WeaponFactory.KATANA.create(material, ItemIds.properties());
	}

	@Override
	public Item addSaber(WeaponMaterial material) 
	{
		return WeaponFactory.SABER.create(material, ItemIds.properties());
	}

	@Override
	public Item addRapier(WeaponMaterial material) 
	{
		return WeaponFactory.RAPIER.create(material, ItemIds.properties());
	}

	@Override
	public Item addGreatsword(WeaponMaterial material) 
	{
		return WeaponFactory.GREATSWORD.create(material, ItemIds.properties());
	}

	@Override
	public Item addBattleHammer(WeaponMaterial material)
	{
		return WeaponFactory.BATTLE_HAMMER.create(material, ItemIds.properties());
	}

	@Override
	public Item addWarhammer(WeaponMaterial material)
	{
		return WeaponFactory.WARHAMMER.create(material, ItemIds.properties());
	}

	@Override
	public Item addSpear(WeaponMaterial material)
	{
		return WeaponFactory.SPEAR.create(material, ItemIds.properties());
	}

	@Override
	public Item addHalberd(WeaponMaterial material) 
	{
		return WeaponFactory.HALBERD.create(material, ItemIds.properties());
	}

	@Override
	public Item addPike(WeaponMaterial material) 
	{
		return WeaponFactory.PIKE.create(material, ItemIds.properties());
	}

	@Override
	public Item addLance(WeaponMaterial material)
	{
		return WeaponFactory.LANCE.create(material, ItemIds.properties());
	}

	@Override
	public Item addLongbow(WeaponMaterial material)
	{
		return WeaponFactory.LONGBOW.create(material, ItemIds.properties());
	}

	@Override
	public Item addHeavyCrossbow(WeaponMaterial material) 
	{
		return WeaponFactory.HEAVY_CROSSBOW.create(material, ItemIds.properties());
	}

	@Override
	public Item addThrowingKnife(WeaponMaterial material)
	{
		return WeaponFactory.THROWING_KNIFE.create(material, ItemIds.properties());
	}

	@Override
	public Item addTomahawk(WeaponMaterial material) 
	{
		return WeaponFactory.TOMAHAWK.create(material, ItemIds.properties());
	}

	@Override
	public Item addJavelin(WeaponMaterial material)
	{
		return WeaponFactory.JAVELIN.create(material, ItemIds.properties());
	}

	@Override
	public Item addBoomerang(WeaponMaterial material)
	{
		return WeaponFactory.BOOMERANG.create(material, ItemIds.properties());
	}

	@Override
	public Item addBattleaxe(WeaponMaterial material) 
	{
		return WeaponFactory.BATTLEAXE.create(material, ItemIds.properties());
	}

	@Override
	public Item addFlangedMace(WeaponMaterial material)
	{
		return WeaponFactory.FLANGED_MACE.create(material, ItemIds.properties());
	}

	@Override
	public Item addGlaive(WeaponMaterial material)
	{
		return WeaponFactory.GLAIVE.create(material, ItemIds.properties());
	}

	@Override
	public Item addQuarterstaff(WeaponMaterial material)
	{
		return WeaponFactory.QUARTERSTAFF.create(material, ItemIds.properties());
	}

	@Override
	public Item addScythe(WeaponMaterial material)
	{
		return WeaponFactory.SCYTHE.create(material, ItemIds.properties());
	}

}
