"""Weaponry common: keybinds, tag reload, commands, recipe condition, particles, menus, loot modifier."""
import re
B = 'common/src/main/java/com/oblivioussp/spartanweaponry/'

def load(p):
    return open(B + p, encoding='utf-8').read()

def save(p, s):
    open(B + p, 'w', encoding='utf-8').write(s)

def rep(s, a, b, p=''):
    if a not in s:
        print('NO MATCH', p, a[:80])
    return s.replace(a, b)

def addimp(s, imp):
    if 'import ' + imp + ';' in s:
        return s
    return re.sub(r'(package [\w.]+;\n)', r'\1\nimport ' + imp + ';', s, count=1)

def strip_neo(s):
    s = re.sub(r'import net\.neoforged\.[^\n]*\n', '', s)
    s = re.sub(r'@EventBusSubscriber\([^\n]*\)\n', '', s)
    s = re.sub(r'[ \t]*@SubscribeEvent(\([^)]*\))?\n', '', s)
    return s

# Key bindings: vanilla KeyMapping (NeoForge's conflict context IN_GAME is set by the NeoForge module)
p = 'client/KeyBinds.java'
s = strip_neo(load(p))
s = rep(s, 'KeyConflictContext.IN_GAME, ', '', p)
s = re.sub(r'\n\t@SubscribeEvent\n.*?\n\t\}\n', '\n', s, flags=re.S)
s = re.sub(r'\tpublic static void registerKeyBinds\(RegisterKeyMappingsEvent ev\)\n\t\{\n\t\tev\.register\(KeyBinds\.KEY_ACCESS_QUIVER\);\n\t\}\n', '', s)
save(p, s)

# Tag reload: NeoForge TagsUpdatedEvent, Fabric CommonLifecycleEvents.TAGS_LOADED
p = 'event/ReloadResourceEventHandler.java'
s = strip_neo(load(p))
s = rep(s, 'public static void onUpdateTags(TagsUpdatedEvent ev)', '/** After the tags are (re)loaded, on the server and on the client. */\n\tpublic static void onUpdateTags()', p)
s = rep(s, 'if(FMLEnvironment.dist == Dist.CLIENT)', 'if(Platform.INSTANCE.isClient())', p)
s = addimp(s, 'com.oblivioussp.spartanweaponry.platform.Platform')
save(p, s)

# Commands
p = 'init/ModCommands.java'
s = strip_neo(load(p))
s = rep(s, 'public static void registerCommands(RegisterCommandsEvent ev)', 'public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher)', p)
s = s.replace('ev.getDispatcher().register(', 'dispatcher.register(')
s = re.sub(r'\tpublic static final Registrar<ArgumentTypeInfo<\?, \?>> ARGUMENT_REGISTRY.*?\n\}\n', '''\t/** The argument types (NeoForge: ArgumentTypeInfos.registerByClass + the registry, Fabric: ArgumentTypeRegistry). */
\tpublic static void registerArgumentTypes(ArgumentRegistrar registrar)
\t{
\t\tregistrar.register("oil", OilArgument.class, SingletonArgumentInfo.contextFree(OilArgument::oil));
\t\tregistrar.register("potion", PotionArgument.class, SingletonArgumentInfo.contextFree(PotionArgument::potion));
\t}
\t
\tpublic interface ArgumentRegistrar
\t{
\t\t<A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void register(String name, Class<A> argumentClass, ArgumentTypeInfo<A, T> info);
\t}
}
''', s, flags=re.S)
s = addimp(s, 'com.mojang.brigadier.CommandDispatcher')
s = addimp(s, 'com.mojang.brigadier.arguments.ArgumentType')
save(p, s)

# Recipe condition: plain record; the loaders wrap it
p = 'api/crafting/condition/TypeDisabledCondition.java'
s = strip_neo(load(p))
s = rep(s, ' implements ICondition', '', p)
s = rep(s, '''	@Override
	public boolean test(IContext context)
	{''', '''	/** The NeoForge ICondition and the Fabric resource condition both test this. */
	public boolean test()
	{''', p)
s = re.sub(r'\t@Override\n\tpublic MapCodec<\? extends ICondition> codec\(\)\n\t\{\n\t\treturn CODEC;\n\t\}\n', '', s)
save(p, s)

p = 'init/ModRecipeSerializers.java'
s = strip_neo(load(p))
s = re.sub(r'\t+public static final Registrar<MapCodec<\? extends ICondition>> CONDITION_REGISTRY[^\n]*\n', '', s)
s = re.sub(r'\t+public static final Registrar\.Entry<MapCodec<\? extends ICondition>[^\n]*\n', '', s)
save(p, s)

# Particles: the providers are registered by the loaders
p = 'init/ModParticles.java'
s = strip_neo(load(p))
s = s.replace('new SimpleParticleType(false)', 'new SimpleParticleType(false) {}')
s = re.sub(r'\tpublic static void registerFactories\(RegisterParticleProvidersEvent ev\)\n\t\{\n(.*?)\n\t\}\n', lambda m: '''\t/** NeoForge: RegisterParticleProvidersEvent#registerSpriteSet, Fabric: ParticleFactoryRegistry. */
\tpublic static void registerFactories(SpriteSetRegistrar ev)
\t{
''' + m.group(1).replace('ev.registerSpriteSet(', 'ev.register(') + '''
\t}
\t
\tpublic interface SpriteSetRegistrar
\t{
\t\t<T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> provider);
\t}
''', s, flags=re.S)
for imp in ['java.util.function.Function', 'net.minecraft.core.particles.ParticleOptions', 'net.minecraft.client.particle.SpriteSet', 'net.minecraft.client.particle.ParticleProvider', 'net.minecraft.core.particles.ParticleType']:
    s = addimp(s, imp)
save(p, s)

# Menus
p = 'init/ModMenus.java'
s = strip_neo(load(p))
s = s.replace('IMenuTypeExtension.create(', 'Platform.INSTANCE.createMenuType(')
s = addimp(s, 'com.oblivioussp.spartanweaponry.platform.Platform')
save(p, s)

for p in ['item/QuiverArrowItem.java', 'item/QuiverBoltItem.java']:
    s = load(p)
    s = rep(s, 'player.openMenu(new ContainerProvider(', 'Platform.INSTANCE.openMenu((ServerPlayer)player, new ContainerProvider(', p)
    s = addimp(s, 'com.oblivioussp.spartanweaponry.platform.Platform')
    s = addimp(s, 'net.minecraft.server.level.ServerPlayer')
    save(p, s)

# Loot modifier: the loaders wrap the rule (NeoForge: a global loot modifier, Fabric: a mixin in LootTable)
p = 'init/ModLootModifiers.java'
s = strip_neo(load(p))
s = re.sub(r'\tpublic static final Registrar<MapCodec<\? extends IGlobalLootModifier>> REGISTRY[^\n]*\n', '', s)
s = re.sub(r'\t[^\n]*Registrar\.Entry<MapCodec<\? extends IGlobalLootModifier>[^\n]*\n', '', s)
save(p, s)
