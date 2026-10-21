package com.oblivioussp.spartanweaponry.fabric.mixin.client;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.oblivioussp.spartanweaponry.client.ClientHelper;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;

/** NeoForge EntityRenderersEvent.CreateSkullModels: the models of the mod's heads. */
@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockRendererMixin
{
	@ModifyReturnValue(method = "createSkullRenderers", at = @At("RETURN"))
	private static Map<SkullBlock.Type, SkullModelBase> spartanweaponry$heads(Map<SkullBlock.Type, SkullModelBase> models, EntityModelSet modelSet)
	{
		Map<SkullBlock.Type, SkullModelBase> all = new HashMap<>(models);
		ClientHelper.registerSkullModels(modelSet, all::put);
		return all;
	}
}
