package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.entity.projectile.DynamiteEntity;
import com.oblivioussp.spartanweaponry.util.Config;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class DynamiteItem extends Item
{
	public DynamiteItem(Properties properties) 
	{
		super(properties);
	}

	@Override
	public InteractionResult use(Level levelIn, Player playerIn, InteractionHand handIn) 
	{
		ItemStack itemstack = playerIn.getItemInHand(handIn);

        if (!playerIn.getAbilities().instabuild)
        {
            itemstack.shrink(1);
        }

        levelIn.playSound((Player)null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (levelIn.random.nextFloat() * 0.4F + 0.8F));
        levelIn.playSound((Player)null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.TNT_PRIMED, SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (!levelIn.isClientSide())
        {
        	DynamiteEntity entityDyanmite = new DynamiteEntity(playerIn, levelIn);
        	entityDyanmite.shootFromRotation(playerIn, playerIn.xRotO, playerIn.yRotO, 0.0F, 0.75F, 1.0F);
        	levelIn.addFreshEntity(entityDyanmite);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn) 
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			tooltip.add(Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".dynamite.desc", (float)Config.INSTANCE.fuseTicksDynamite.get() / 20.0f).withStyle(ChatFormatting.GRAY));
			if(Config.INSTANCE.disableTerrainDamage.get())
				tooltip.add(Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".dynamite.no_terrain_damage").withStyle(ChatFormatting.GRAY));
			else
				tooltip.add(Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".dynamite.terrain_damage").withStyle(ChatFormatting.GRAY));
			tooltip.flush();
			super.appendHoverText(stack, levelIn, display, tooltipOut, flagIn);
		}
		finally
		{
			tooltip.flush();
		}
	}
}
