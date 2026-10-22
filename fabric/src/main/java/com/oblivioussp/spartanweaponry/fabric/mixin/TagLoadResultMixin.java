package com.oblivioussp.spartanweaponry.fabric.mixin;

import java.util.Collection;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.oblivioussp.spartanweaponry.fabric.LoadedTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagManager;

/** Keeps the tags of each reload with their entries, for the tag_not_empty recipe condition. */
@Mixin(TagManager.LoadResult.class)
public abstract class TagLoadResultMixin
{
	@Inject(method = "<init>", at = @At("RETURN"))
	private void spartanweaponry$capture(ResourceKey<?> key, Map<Identifier, ? extends Collection<?>> tags, CallbackInfo ci)
	{
		LoadedTags.put(key, tags);
	}
}
