package com.oblivioussp.spartanweaponry.item.crafting;

import net.minecraft.core.registries.Registries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** 8 projectiles around a lingering potion: 8 tipped projectiles ({"projectile": item, "result": item}) */
public class TippedProjectileBaseRecipe extends CustomRecipe 
{
	protected final Item projectileIn;
	protected final Item projectileOut;

	public TippedProjectileBaseRecipe(Item arrowIn, Item arrowOut) 
	{
		super(CraftingBookCategory.MISC);
		this.projectileIn = arrowIn;
		this.projectileOut = arrowOut;
	}

	@Override
	public boolean matches(CraftingInput container, Level level) 
	{
		if(container.width() == 3 && container.height() == 3)
		{
			for(int i = 0; i < container.width(); i++)
			{
				for(int j = 0; j < container.height(); j++)
				{
					ItemStack stack = container.getItem(i, j);
					
					if(stack.isEmpty())		
						return false;
					
					Item item = stack.getItem();
					
					if(i == 1 && j == 1) 
					{
						if(item != Items.LINGERING_POTION)	
							return false;
					}
					else if(item != projectileIn)	
						return false;
				}
			}
			return true;
		}
		else
			return false;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registry)
	{
		ItemStack potionStack = inv.getItem(1, 1);
		if(potionStack.getItem() == Items.LINGERING_POTION)
		{
			PotionContents contents = potionStack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
			if(contents.potion().isPresent() && !contents.potion().get().value().getEffects().isEmpty())
			{
				ItemStack arrowResult = new ItemStack(projectileOut, 8);
				PotionHelper.setPotion(arrowResult, contents.potion().get());
				return arrowResult;
			}
		}
		return ItemStack.EMPTY;
	}

	

	@Override
	public RecipeSerializer<TippedProjectileBaseRecipe> getSerializer()
	{
		return ModRecipeSerializers.TIPPED_PROJECTILE_BASE.get();
	}

	public static class Serializer implements RecipeSerializer<TippedProjectileBaseRecipe>
	{
		private static final MapCodec<TippedProjectileBaseRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
				BuiltInRegistries.ITEM.byNameCodec().fieldOf("projectile").forGetter((recipe) -> recipe.projectileIn),
				BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter((recipe) -> recipe.projectileOut))
				.apply(instance, TippedProjectileBaseRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, TippedProjectileBaseRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM), (recipe) -> recipe.projectileIn,
				ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM), (recipe) -> recipe.projectileOut,
				TippedProjectileBaseRecipe::new);

		public Serializer() {}

		@Override
		public MapCodec<TippedProjectileBaseRecipe> codec()
		{
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, TippedProjectileBaseRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
