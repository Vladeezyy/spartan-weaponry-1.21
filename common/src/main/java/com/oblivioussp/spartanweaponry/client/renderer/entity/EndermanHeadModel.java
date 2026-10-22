package com.oblivioussp.spartanweaponry.client.renderer.entity;


import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class EndermanHeadModel extends SkullModel
{
	private static final String PART_HEAD = "head";
	private static final String PART_JAW = "jaw";
	
	
	public EndermanHeadModel(ModelPart modelRoot)
	{
		super(modelRoot);
	}
	
	public static LayerDefinition createLayer()
	{
		MeshDefinition meshDef = new MeshDefinition();
		PartDefinition partDef = meshDef.getRoot();
		
		partDef.addOrReplaceChild(PART_HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
		partDef.getChild(PART_HEAD).addOrReplaceChild(PART_JAW, CubeListBuilder.create().texOffs(0, 16).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(-0.5f)), PartPose.ZERO);
		
		return LayerDefinition.create(meshDef, 32, 32);
	}


}
