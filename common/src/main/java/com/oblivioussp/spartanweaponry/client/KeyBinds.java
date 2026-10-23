package com.oblivioussp.spartanweaponry.client;

import com.mojang.blaze3d.platform.InputConstants;


import com.mojang.blaze3d.platform.InputConstants.Type;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class KeyBinds
{
	// 1.21.9+: a category the loader registers (NeoForge RegisterKeyMappingsEvent, Fabric KeyMapping.Category.register);
	// its name is key.category.spartanweaponry.title
	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "title"));
	public static KeyMapping KEY_ACCESS_QUIVER = new KeyMapping("key." + ModSpartanWeaponry.ID + ".access_quiver", Type.KEYBOARD, InputConstants.KEY_I, CATEGORY);

}
