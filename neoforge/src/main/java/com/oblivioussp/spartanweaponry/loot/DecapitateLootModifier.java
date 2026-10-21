package com.oblivioussp.spartanweaponry.loot;

import net.minecraft.core.registries.Registries;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;

import com.oblivioussp.spartanweaponry.util.ItemNbt;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.api.IWeaponTraitContainer;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.minecraft.core.registries.BuiltInRegistries;

public class DecapitateLootModifier extends LootModifier 
{
	public static final MapCodec<DecapitateLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance).and(
				BuiltInRegistries.ITEM.byNameCodec().fieldOf("skull").forGetter(modifier -> modifier.skull)
			).apply(instance, DecapitateLootModifier::new));
	
	private Item skull;

	public DecapitateLootModifier(LootItemCondition[] conditionsIn, Item skullItem)
	{
		super(conditionsIn);
		skull = skullItem;
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		Decapitation.apply(generatedLoot, context, skull);
		return generatedLoot;
	}

/*	public static class Serializer extends GlobalLootModifierSerializer<DecapitateLootModifier>
	{
		
		@Override
		public DecapitateLootModifier read(ResourceLocation location, JsonObject object,
				LootItemCondition[] lootConditions) 
		{
			Item skullItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(GsonHelper.getAsString(object, "skull")));
			return new DecapitateLootModifier(lootConditions, skullItem);
		}

		@Override
		public JsonObject write(DecapitateLootModifier instance)
		{
			JsonObject result = this.makeConditions(instance.conditions);
			result.addProperty("skull", BuiltInRegistries.ITEM.getKey(instance.skull).toString());
			return result;
		}
		
	}*/

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() 
	{
		return CODEC;
	}
}
