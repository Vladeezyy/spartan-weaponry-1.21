package com.oblivioussp.spartanweaponry.client.gui.components;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * The 1.20.1 ImageButton: a region of a GUI texture, the hovered version yDiffTex below it. 1.21's ImageButton takes
 * sprites from the GUI atlas instead, so the mod's own GUI textures need this.
 */
public class TexturedButton extends Button
{
	protected final Identifier texture;
	protected final int xTexStart, yTexStart, yDiffTex;
	protected final int textureWidth, textureHeight;

	public TexturedButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier texture,
			int textureWidth, int textureHeight, OnPress onPress, Component message)
	{
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
		this.texture = texture;
		this.xTexStart = xTexStart;
		this.yTexStart = yTexStart;
		this.yDiffTex = yDiffTex;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		int v = yTexStart;
		if(!isActive())
			v += yDiffTex * 2;
		else if(isHoveredOrFocused())
			v += yDiffTex;
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), xTexStart, v, width, height, textureWidth, textureHeight);
	}
}
