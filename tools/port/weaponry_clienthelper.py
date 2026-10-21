import re
p='common/src/main/java/com/oblivioussp/spartanweaponry/client/ClientHelper.java'
s=open(p,encoding='utf-8').read()
s=re.sub(r'import net\.neoforged\.[^\n]*\n','',s)
s=re.sub(r'import top\.theillusivec4\.[^\n]*\n','',s)
s=s.replace('import com.oblivioussp.spartanweaponry.client.model.CurioRenderer;\n','')
s=re.sub(r'@EventBusSubscriber\([^\n]*\)\n','/** The client registrations of the mod; NeoForge and Fabric call these from their client setup. */\n',s)
s=re.sub(r'[ \t]*@SubscribeEvent(\([^)]*\))?\n','',s)
def rep(a,b):
    global s
    if a not in s: print('NO MATCH',a[:80])
    s=s.replace(a,b)
rep('public static void registerItemColoursHandler(RegisterColorHandlersEvent.Item ev)','public static void registerItemColoursHandler(BiConsumer<ItemColor, Item[]> ev)')
rep('ev.register(COLOR_TIPPED_PROJECTILE, ModItems.TIPPED_WOODEN_ARROW.get(), ','ev.accept(COLOR_TIPPED_PROJECTILE, new Item[] {ModItems.TIPPED_WOODEN_ARROW.get(), ')
rep('ModItems.TIPPED_NETHERITE_BOLT.get());','ModItems.TIPPED_NETHERITE_BOLT.get()});')
rep('ev.register(COLOR_OIL, ModItems.WEAPON_OIL.get());','ev.accept(COLOR_OIL, new Item[] {ModItems.WEAPON_OIL.get()});')
s=re.sub(r'	public static void registerCurioRenders\(\)\n	\{\n.*?\n	\}\n','',s,flags=re.S)
s=re.sub(r'	public static void reload\(RegisterClientReloadListenersEvent ev\)\n	\{\n.*?\n	\}\n','',s,flags=re.S)
s=s.replace('ItemProperties.register(','itemProperty(')
rep('	public static void registerPendingItemProperties()\n	{','''	/** ItemProperties.register (NeoForge makes it public, Fabric has its own registry for it). */
	@FunctionalInterface
	public interface ItemPropertyRegistrar
	{
		void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property);
	}

	private static ItemPropertyRegistrar propertyRegistrar;

	private static void itemProperty(Item item, ResourceLocation name, ClampedItemPropertyFunction property)
	{
		propertyRegistrar.register(item, name, property);
	}

	public static void registerPendingItemProperties(ItemPropertyRegistrar registrar)
	{
		propertyRegistrar = registrar;''')
rep('public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers ev)','public static void registerEntityRenders(RendererRegistrar ev)')
s=s.replace('ev.registerEntityRenderer(','ev.entity(').replace('ev.registerBlockEntityRenderer(','ev.blockEntity(')
rep('public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions ev)','public static void registerModelLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> ev)')
s=s.replace('ev.registerLayerDefinition(','ev.accept(')
rep('''	public static void registerSkullModels(EntityRenderersEvent.CreateSkullModels ev)
	{
		EntityModelSet entityModelSet = ev.getEntityModelSet();''','''	/** NeoForge: EntityRenderersEvent.CreateSkullModels, Fabric: a mixin in SkullBlockRenderer.createSkullRenderers. */
	public static void registerSkullModels(EntityModelSet entityModelSet, BiConsumer<SkullBlock.Type, SkullModelBase> ev)
	{''')
s=s.replace('ev.registerSkullModel(','ev.accept(')
rep('public static void registerScreens(RegisterMenuScreensEvent ev)','public static void registerScreens(ScreenRegistrar ev)')
rep('''	public static void registerHudOverlays(RegisterGuiLayersEvent ev)
	{
		ev.registerAboveAll(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "load_state"), LOAD_STATE);
		ev.registerAboveAll(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_ammo"), QUIVER_AMMO);
		ev.registerAboveAll(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil_uses"), OIL_USES);
		ev.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "crosshair"), NEW_CROSSHAIR);
	}''','''	/** The HUD layers: above everything (aboveAll) or right above the vanilla crosshair (aboveCrosshair). */
	public static void registerHudOverlays(BiConsumer<ResourceLocation, LayeredDraw.Layer> aboveAll, BiConsumer<ResourceLocation, LayeredDraw.Layer> aboveCrosshair)
	{
		aboveAll.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "load_state"), LOAD_STATE);
		aboveAll.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_ammo"), QUIVER_AMMO);
		aboveAll.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil_uses"), OIL_USES);
		aboveCrosshair.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "crosshair"), NEW_CROSSHAIR);
	}''')
rep('''	public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent ev)
	{
		ev.register(QuiverTooltip.class, ClientQuiverTooltip::new);
		ev.register(OilCoatingTooltip.class, ClientOilCoatingTooltip::new);
	}''','''	/** The tooltip pictures of quivers and oiled weapons. */
	public static ClientTooltipComponent createTooltipComponent(TooltipComponent component)
	{
		if(component instanceof QuiverTooltip quiver)
			return new ClientQuiverTooltip(quiver);
		if(component instanceof OilCoatingTooltip oil)
			return new ClientOilCoatingTooltip(oil);
		return null;
	}

	/** Entity and block entity renderers (EntityRenderers / BlockEntityRenderers.register). */
	public interface RendererRegistrar
	{
		<T extends Entity> void entity(EntityType<? extends T> type, EntityRendererProvider<T> provider);

		<T extends BlockEntity> void blockEntity(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider);
	}

	/** MenuScreens.register. */
	public interface ScreenRegistrar
	{
		<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor);
	}''')
imps=['java.util.function.BiConsumer','java.util.function.Supplier','net.minecraft.client.renderer.item.ClampedItemPropertyFunction','net.minecraft.client.model.geom.ModelLayerLocation','net.minecraft.client.model.geom.builders.LayerDefinition','net.minecraft.client.model.SkullModelBase','net.minecraft.world.level.block.SkullBlock','net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent','net.minecraft.world.inventory.tooltip.TooltipComponent','net.minecraft.world.entity.Entity','net.minecraft.world.entity.EntityType','net.minecraft.client.renderer.entity.EntityRendererProvider','net.minecraft.world.level.block.entity.BlockEntity','net.minecraft.world.level.block.entity.BlockEntityType','net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider','net.minecraft.world.inventory.AbstractContainerMenu','net.minecraft.client.gui.screens.Screen','net.minecraft.client.gui.screens.inventory.MenuAccess','net.minecraft.world.inventory.MenuType','net.minecraft.client.gui.screens.MenuScreens','net.minecraft.client.gui.LayeredDraw']
for imp in imps:
    if 'import '+imp+';' not in s:
        s=s.replace('package com.oblivioussp.spartanweaponry.client;\n','package com.oblivioussp.spartanweaponry.client;\n\nimport '+imp+';\n',1)
open(p,'w',encoding='utf-8').write(s)
