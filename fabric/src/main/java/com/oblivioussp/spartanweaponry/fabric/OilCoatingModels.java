package com.oblivioussp.spartanweaponry.fabric;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.oblivioussp.spartanweaponry.capability.OilHandler;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/**
 * The oil coating of the weapons on Fabric (NeoForge: the spartanweaponry:oil_coated_item geometry loader): a model
 * with a "coating" texture gets that texture as one more generated item layer (tint index 100, the oil colour), drawn
 * only while the weapon is oiled.
 */
public final class OilCoatingModels
{
	private static final int COATING_LAYER = 100;
	private static final ItemModelGenerator GENERATOR = new ItemModelGenerator();
	private static final FaceBakery FACE_BAKERY = new FaceBakery();

	private OilCoatingModels() {}

	public static void register()
	{
		ModelLoadingPlugin.register(plugin -> plugin.modifyModelAfterBake().register((model, context) ->
		{
			if(model == null || !(context.sourceModel() instanceof BlockModel source) || !source.hasTexture("coating"))
				return model;
			TextureAtlasSprite sprite = context.textureGetter().apply(source.getMaterial("coating"));
			return new Coated(model, bakeCoating(sprite, context.settings()));
		}));
	}

	private static List<BakedQuad> bakeCoating(TextureAtlasSprite sprite, ModelState state)
	{
		List<BakedQuad> quads = new ArrayList<>();
		for(BlockElement element : GENERATOR.processFrames(COATING_LAYER, "coating", sprite.contents()))
		{
			for(var entry : element.faces.entrySet())
			{
				Direction direction = entry.getKey();
				BlockElementFace face = entry.getValue();
				quads.add(FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, state, element.rotation, element.shade));
			}
		}
		return quads;
	}

	private static RenderMaterial standardMaterial;

	private static RenderMaterial standardMaterial()
	{
		if(standardMaterial == null)
			standardMaterial = RendererAccess.INSTANCE.getRenderer().materialFinder().find();
		return standardMaterial;
	}

	private static final class Coated extends ForwardingBakedModel
	{
		private final List<BakedQuad> coating;

		Coated(BakedModel model, List<BakedQuad> coating)
		{
			this.wrapped = model;
			this.coating = coating;
		}

		@Override
		public boolean isVanillaAdapter()
		{
			return false;
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context)
		{
			super.emitItemQuads(stack, randomSupplier, context);
			if(!OilHandler.isOiled(stack))
				return;
			QuadEmitter emitter = context.getEmitter();
			for(BakedQuad quad : coating)
			{
				emitter.fromVanilla(quad, standardMaterial(), null);
				emitter.emit();
			}
		}
	}
}
