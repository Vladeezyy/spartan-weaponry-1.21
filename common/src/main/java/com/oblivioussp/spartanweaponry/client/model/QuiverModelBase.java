package com.oblivioussp.spartanweaponry.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;



/**
 * 1.21.2+: a model with a render state, here the number of arrows or bolts to show (1.21.1: renderToBuffer and
 * renderArrows); the arrow parts are hidden in setupAnim and the whole root is drawn.
 */
public abstract class QuiverModelBase extends Model<Integer>
{
	protected final ModelPart root;
	
	public QuiverModelBase(ModelPart rootModel) 
	{
		super(rootModel, RenderTypes::entityCutoutNoCull);
		root = rootModel;
	}
}
