package com.oblivioussp.spartanweaponry.client.gui.components;


import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ToggleImageButton extends Button
{
	protected final Identifier textureLocation;
	protected final int texStartU, texStartV;
	protected final int toggleDiffU, hoverDiffV;
	protected final int textureWidth, textureHeight;
	private boolean toggleState;

	public ToggleImageButton(boolean isToggled, int xPos, int yPos, int width, int height, int texU, int texV,
			int texToggleDiffU, int texHoverDiffV, Identifier textureLoc, int texWidth, int texHeight, OnPress onPress,
			Component component) 
	{
		super(xPos, yPos, width, height, component, onPress, DEFAULT_NARRATION);
		texStartU = texU;
		texStartV = texV;
		toggleDiffU = texToggleDiffU;
		hoverDiffV = texHoverDiffV;
		textureLocation = textureLoc;
		textureWidth = texWidth;
		textureHeight = texHeight;
		toggleState = isToggled;
	}
	
	@Override
	public void onPress(InputWithModifiers input)
	{
		toggleState = !toggleState;
		super.onPress(input);
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor guiGraphics, int renderX, int renderY, float p_94285_) 
	{
		extractDefaultSprite(guiGraphics);
		int u = texStartU;
		int v = texStartV;
		
		if(toggleState)
			u += toggleDiffU;
		if(isHovered())
			v += hoverDiffV;
		
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, textureLocation, getX(), getY(), u, v, width, height, textureWidth, textureHeight);
//		if(isHovered)
//			renderToolTip(guiGraphics, renderX, renderY);
	}
}
