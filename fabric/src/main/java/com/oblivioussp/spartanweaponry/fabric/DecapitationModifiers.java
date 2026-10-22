package com.oblivioussp.spartanweaponry.fabric;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.loot.Decapitation;
import com.oblivioussp.spartanweaponry.util.Log;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * The decapitate global loot modifiers on Fabric: the same data as NeoForge (the entries of
 * data/neoforge/loot_modifiers/global_loot_modifiers.json of type spartanweaponry:decapitate), applied by
 * mixin/LootTableMixin. The conditions need the registries, so they are read on the first roll after a reload.
 */
public final class DecapitationModifiers implements SimpleSynchronousResourceReloadListener
{
	public static final DecapitationModifiers INSTANCE = new DecapitationModifiers();
	private static final Identifier LIST = Identifier.fromNamespaceAndPath("neoforge", "loot_modifiers/global_loot_modifiers.json");
	private static final String TYPE = ModSpartanWeaponry.ID + ":decapitate";

	private record Modifier(List<LootItemCondition> conditions, Item skull) {}

	private List<JsonObject> json = List.of();
	private volatile List<Modifier> modifiers;

	private DecapitationModifiers() {}

	@Override
	public Identifier getFabricId()
	{
		return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "decapitate_loot_modifiers");
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager)
	{
		// The entries of every pack, in order; "replace" drops the ones before (as NeoForge's LootModifierManager)
		List<Identifier> entries = new ArrayList<>();
		for(Resource resource : manager.getResourceStack(LIST))
		{
			try(Reader reader = resource.openAsReader())
			{
				JsonObject list = JsonParser.parseReader(reader).getAsJsonObject();
				if(list.has("replace") && list.get("replace").getAsBoolean())
					entries.clear();
				for(JsonElement entry : list.getAsJsonArray("entries"))
				{
					Identifier id = Identifier.parse(entry.getAsString());
					if(!entries.contains(id))
						entries.add(id);
				}
			}
			catch(Exception e)
			{
				Log.error("Couldn't read the global loot modifier list of " + resource.sourcePackId() + ": " + e);
			}
		}
		List<JsonObject> found = new ArrayList<>();
		for(Identifier id : entries)
		{
			Optional<Resource> resource = manager.getResource(id.withPath(path -> "loot_modifiers/" + path + ".json"));
			if(resource.isEmpty())
				continue;
			try(Reader reader = resource.get().openAsReader())
			{
				JsonObject modifier = JsonParser.parseReader(reader).getAsJsonObject();
				if(TYPE.equals(modifier.get("type").getAsString()))
					found.add(modifier);
			}
			catch(Exception e)
			{
				Log.error("Couldn't read the loot modifier " + id + ": " + e);
			}
		}
		json = found;
		modifiers = null;
	}

	/** Adds the heads to the drops of a loot table rolled directly. */
	public void apply(LootContext context, List<ItemStack> drops)
	{
		List<Modifier> list = modifiers;
		if(list == null)
			modifiers = list = parse(context);
		if(list.isEmpty())
			return;
		ObjectArrayList<ItemStack> loot = null;
		for(Modifier modifier : list)
		{
			boolean pass = true;
			for(LootItemCondition condition : modifier.conditions())
			{
				if(!condition.test(context))
				{
					pass = false;
					break;
				}
			}
			if(!pass)
				continue;
			if(loot == null)
				loot = new ObjectArrayList<>(drops);
			Decapitation.apply(loot, context, modifier.skull());
		}
		if(loot != null && loot.size() != drops.size())
			drops.addAll(loot.subList(drops.size(), loot.size()));
	}

	private List<Modifier> parse(LootContext context)
	{
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, context.getLevel().registryAccess());
		List<Modifier> list = new ArrayList<>();
		for(JsonObject modifier : json)
		{
			try
			{
				List<LootItemCondition> conditions = LootItemCondition.DIRECT_CODEC.listOf().parse(ops, modifier.get("conditions")).getOrThrow();
				Item skull = BuiltInRegistries.ITEM.getValue(Identifier.parse(modifier.get("skull").getAsString()));
				list.add(new Modifier(conditions, skull));
			}
			catch(Exception e)
			{
				Log.error("Couldn't read a decapitate loot modifier: " + e);
			}
		}
		return list;
	}
}
