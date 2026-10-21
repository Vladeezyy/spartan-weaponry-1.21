package com.oblivioussp.spartanweaponry.fabric.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.oblivioussp.spartanweaponry.fabric.DecapitationModifiers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * NeoForge applies the global loot modifiers to what a loot table rolls; here the decapitate ones, for the tables
 * rolled directly (all of vanilla's ways to roll a table end in getRandomItemsRaw; a table inside another is left alone).
 */
@Mixin(LootTable.class)
public abstract class LootTableMixin
{
	@Unique
	private static final ThreadLocal<int[]> SPARTANWEAPONRY$DEPTH = ThreadLocal.withInitial(() -> new int[1]);

	@WrapMethod(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
	private void spartanweaponry$lootModifiers(LootContext context, Consumer<ItemStack> output, Operation<Void> original)
	{
		int[] depth = SPARTANWEAPONRY$DEPTH.get();
		if(depth[0] > 0)
		{
			depth[0]++;
			try
			{
				original.call(context, output);
			}
			finally
			{
				depth[0]--;
			}
			return;
		}
		List<ItemStack> drops = new ArrayList<>();
		depth[0]++;
		try
		{
			original.call(context, (Consumer<ItemStack>)drops::add);
		}
		finally
		{
			depth[0]--;
		}
		DecapitationModifiers.INSTANCE.apply(context, drops);
		drops.forEach(output);
	}
}
