package com.oblivioussp.spartanweaponry.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.client.KeyMapping;

public class KeyBinds
{
	public static KeyMapping KEY_ACCESS_QUIVER = new KeyMapping("key." + ModSpartanWeaponry.ID + ".access_quiver", Type.KEYSYM, GLFW.GLFW_KEY_I, "key." + ModSpartanWeaponry.ID + ".category.title");

}
