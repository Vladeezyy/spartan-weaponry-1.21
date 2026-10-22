package com.oblivioussp.spartanweaponry.fabric.mixin.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oblivioussp.spartanweaponry.client.ClientHelper;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;

/**
 * NeoForge's EntityRenderersEvent.CreateSkullModels: the models and textures of the extra mob heads. 1.21.11 makes a
 * skull model when the block entity renderer or a head item model asks for one (createModel, vanilla types only).
 */
@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockRendererMixin
{
	@Shadow
	@Final
	private static Map<SkullBlock.Type, Identifier> SKIN_BY_TYPE;

	private static Map<SkullBlock.Type, Function<EntityModelSet, SkullModelBase>> spartanweaponry$models;

	@Inject(method = "createModel", at = @At("HEAD"), cancellable = true)
	private static void spartanweaponry$heads(EntityModelSet modelSet, SkullBlock.Type type, CallbackInfoReturnable<SkullModelBase> callback)
	{
		if(spartanweaponry$models == null)
		{
			Map<SkullBlock.Type, Function<EntityModelSet, SkullModelBase>> models = new HashMap<>();
			ClientHelper.registerSkullModels((skull, model, texture) ->
			{
				models.put(skull, model);
				SKIN_BY_TYPE.put(skull, texture);
			});
			spartanweaponry$models = models;
		}
		Function<EntityModelSet, SkullModelBase> model = spartanweaponry$models.get(type);
		if(model != null)
			callback.setReturnValue(model.apply(modelSet));
	}
}
