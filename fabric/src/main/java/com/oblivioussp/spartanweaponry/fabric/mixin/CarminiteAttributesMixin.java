package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.oblivioussp.spartanweaponry.fabric.FabricItemHooks;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.item.LongbowItem;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * The weapons' modifiers for carminite (the NeoForge layer of Twilight Forest on Fabric). Its ItemStack mixin replaces
 * both forEachModifier methods (@WrapMethod without the original), so ItemStackMixin#spartanweaponry$attributes never
 * runs there; it asks Item#carminite$getDefaultAttributeModifiers(ItemStack) instead when the stack has no modifiers of
 * its own. This method overrides that default of its IItemExtension; without carminite nothing calls it.
 */
@Mixin({SwordBaseItem.class, ThrowingWeaponItem.class, LongbowItem.class, HeavyCrossbowItem.class})
public abstract class CarminiteAttributesMixin
{
	public ItemAttributeModifiers carminite$getDefaultAttributeModifiers(ItemStack stack)
	{
		return FabricItemHooks.getDefaultAttributeModifiers(stack);
	}
}
