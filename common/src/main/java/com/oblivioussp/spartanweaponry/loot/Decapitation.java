package com.oblivioussp.spartanweaponry.loot;

import com.oblivioussp.spartanweaponry.api.IWeaponTraitContainer;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/**
 * The decapitate trait: the mob drops its head. NeoForge runs it from a global loot modifier
 * (DecapitateLootModifier, data in loot_modifiers), Fabric from a mixin in LootTable with the same data.
 */
public final class Decapitation
{
	private Decapitation() {}

	public static void apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context, Item skull)
	{
		if(!context.hasParam(LootContextParams.ATTACKING_ENTITY))
			return;
		
		Entity killer = context.getParam(LootContextParams.ATTACKING_ENTITY);
		if(killer instanceof LivingEntity living)
		{
			ItemStack weapon = living.getMainHandItem();
//			ItemStack weapon = context.get(LootContextParams.TOOL);
			
			// TODO: See if there is a way that this weapon (above) can be retrieved in a dual-wielding friendly way.
			if(context.getRandom().nextDouble() < WeaponTraits.DECAPITATE.get().getMagnitude() / 100.0f && weapon.getItem() instanceof IWeaponTraitContainer<?> container)
			{
				if(container.hasWeaponTraitWithType(WeaponTraits.TYPE_DECAPITATE))
				{
					ItemStack skullStack = new ItemStack(skull);
					Entity thisEntity = context.getParam(LootContextParams.THIS_ENTITY);
					if(thisEntity instanceof Player player)
					{
						// The player's head shows the player (1.20.1: SkullOwner NBT; 1.21: the profile component)
						skullStack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
					}
					generatedLoot.add(skullStack);
				}
			}
		}
	}
}
