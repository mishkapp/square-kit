package com.mishkapp.minecraft.plugins.squarekit;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.commands.*;
import com.mishkapp.minecraft.plugins.squarekit.commands.area.*;
import com.mishkapp.minecraft.plugins.squarekit.commands.warp.AddPoint;
import com.mishkapp.minecraft.plugins.squarekit.commands.warp.Info;
import com.mishkapp.minecraft.plugins.squarekit.commands.warp.RemovePoint;
import com.mishkapp.minecraft.plugins.squarekit.commands.warp.Tp;
import com.mishkapp.minecraft.plugins.squarekit.listeners.KitListener;
import com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors.BattleInterceptor;
import com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors.EventInterceptor;
import com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors.WorldChangeListener;
import com.mishkapp.minecraft.plugins.squarekit.serializers.ItemStackSerializer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.active.HideShadow;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.MagicImbueWeapon;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.ShadowCloud;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.SpiritsBurden;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.bow.ArrowDamage;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.bow.EntanglingArrow;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.Invisibility;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingInvisibility;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingJumpBoost;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingNightVision;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.passive.ArrowGenerator;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.passive.AstralVision;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.passive.Panic;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.stats.*;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.stats.holding.*;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.use.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bson.Document;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by mishkapp on 27.04.2016.
 */
@Plugin(id = "squarekit", name = "Square Kit", version = "1.0")
public class SquareKit{

    private static SquareKit instance;

    private MongoClient mongoClient;
    private MongoDatabase mongoDb;

    @Inject
    private static Logger logger;

    @Inject
    private PluginContainer plugin;

    @Inject
    private GuiceObjectMapperFactory factory;
    private TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Game game;

    @Listener
    public void onInit(GameInitializationEvent event){
        instance = this;
        initMongo();
        initSerializers();
        initCmds();
        initMessages();
        saveConf();
        registerSuffixes();
        registerListeners();
        registerKits();
    }

    @Listener
    public void onGameStarting(GameStartingServerEvent event){
        initConfigs();
        TopStreakerBar.getInstance().init();
        WarpZonesRegistry.getInstance().init();
        initAreas();
        getPlayersRegistry().updateAllPlayers();
    }

    @Listener
    public void onGameStopping(GameStartingServerEvent event){
        getPlayersRegistry().savePlayers();
    }

    private void initMongo(){
        mongoClient = new MongoClient(
                new ServerAddress("s7.squareland.ru", 27017),
                Collections.singletonList(MongoCredential.createScramSha1Credential("squarekit", "squarekit", "Pcy7F7Y9BBEgqzrA".toCharArray()))
        );
        mongoDb = mongoClient.getDatabase("squarekit");
    }

//    public void onServer() {
//        getSuffixRegistry().purge();
//        getPlayersRegistry().purge();
//        getKitRegistry().purge();
//    }

    private void initAreas(){
        MongoCollection collection = mongoDb.getCollection("areas");
        MongoCursor cursor = collection.find().iterator();
        while (cursor.hasNext()){
            AreaRegistry.getInstance().add(Area.fromDocument((Document) cursor.next()));
        }
    }

    private void initConfigs(){
        ConfigProvider.getInstance().init(mongoDb);
    }

    private void initMessages(){
        Path messagesPath = getConfigDir().resolve("messages.conf");

        try {
            Files.createDirectories(getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(messagesPath.toFile().exists()){
            messagesPath.toFile().delete();
        }
        Asset asset = game.getAssetManager().getAsset(plugin, "messages.conf").orElse(null);
        try {
            asset.copyToFile(messagesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigurationNode cn = null;
        try {
            cn = HoconConfigurationLoader.builder()
                    .setPath(messagesPath)
                    .build()
                    .load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Messages.init(cn);
    }

    private void initSerializers(){
        serializers.registerType(TypeToken.of(ItemStack.class), new ItemStackSerializer());
    }

    private void initCmds(){
        // /kit
        CommandSpec kitCmd = CommandSpec.builder()
                .description(Text.of("Get specified kit"))
                .permission("squarekit.admin")
                .executor(new KitCommand())
                .arguments(
                        GenericArguments.string(Text.of("kitId"))
                )
                .build();

        Sponge.getCommandManager().register(this, kitCmd, "kit");

        // /kits
        CommandSpec kitsCmd = CommandSpec.builder()
                .description(Text.of("List all kits"))
                .executor(new KitsCommand())
                .build();

        Sponge.getCommandManager().register(this, kitsCmd, "kits");

        // /update
        CommandSpec updateCmd = CommandSpec.builder()
                .description(Text.of("Update player"))
                .permission("squarekit.update")
                .executor(new UpdateCommand())
                .build();

        Sponge.getCommandManager().register(this, updateCmd, "update");

        // /stats
        CommandSpec statsCmd = CommandSpec.builder()
                .description(Text.of("Get stats for player"))
                .executor(new StatsCommand())
                .arguments(
                        GenericArguments.string(Text.of("player"))
                )
                .build();

        Sponge.getCommandManager().register(this, statsCmd, "stats");

        // /buildmode
        CommandSpec buildModeCmd = CommandSpec.builder()
                .description(Text.of("Toggle build mode"))
                .executor(new BuildModeCommand())
                .build();

        Sponge.getCommandManager().register(this, buildModeCmd, "buildmode");

        // /warp
        CommandSpec warpAddPoint = CommandSpec.builder()
                .description(Text.of("Adds point to warp list"))
                .executor(new AddPoint())
                .arguments(
                        GenericArguments.string(Text.of("id"))
                )
                .build();

        CommandSpec warpInfo = CommandSpec.builder()
                .description(Text.of("Get info about warp list"))
                .executor(new Info())
                .arguments(
                        GenericArguments.string(Text.of("id"))
                )
                .build();

        CommandSpec warpRemovePoint = CommandSpec.builder()
                .description(Text.of("Removes point from warp list"))
                .executor(new RemovePoint())
                .arguments(
                        GenericArguments.string(Text.of("id")),
                        GenericArguments.string(Text.of("pointId"))
                )
                .build();

        CommandSpec warpTp = CommandSpec.builder()
                .description(Text.of("Teleport to warp"))
                .executor(new Tp())
                .arguments(
                        GenericArguments.string(Text.of("id"))
                )
                .build();

        CommandSpec warpCmd = CommandSpec.builder()
                .description(Text.of("Warp command"))
                .permission("squarekit.admin")
                .child(warpAddPoint, "add")
                .child(warpInfo, "info")
                .child(warpRemovePoint, "remove")
                .child(warpTp, "tp")
                .build();

        Sponge.getCommandManager().register(this, warpCmd, "warp");

//        // /setspawn
//        CommandSpec setSpawnCmd = CommandSpec.builder()
//                .description(Text.of("Set global spawn where player stands"))
//                .permission("squarekit.admin")
//                .executor(new SetSpawnCommand())
//                .build();
//
//        Sponge.getCommandManager().register(this, setSpawnCmd, "setspawn");

//        // /spawn
//        CommandSpec spawnCmd = CommandSpec.builder()
//                .description(Text.of("Sends player to spawn"))
//                .executor(new SpawnCommand())
//                .build();
//
//        Sponge.getCommandManager().register(this, spawnCmd, "spawn");

        // /area
        CommandSpec areaLoad = CommandSpec.builder()
                .description(Text.of("Load area from database"))
                .executor(new LoadCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId"))
                )
                .build();

        CommandSpec areaSave = CommandSpec.builder()
                .description(Text.of("Save area to database"))
                .executor(new SaveCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId"))
                )
                .build();

        CommandSpec areaClearHandlers = CommandSpec.builder()
                .description(Text.of("Remove all handlers from area"))
                .executor(new ClearHandlersCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId"))
                )
                .build();

        CommandSpec areaAddHandler = CommandSpec.builder()
                .description(Text.of("Add handler to area"))
                .executor(new AddHandlerCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId")),
                        GenericArguments.string(Text.of("handlerCode"))
                )
                .build();

        CommandSpec areaSetSafe = CommandSpec.builder()
                .description(Text.of("Set area as safe zone"))
                .executor(new SetSafeCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId"))
                )
                .build();

        CommandSpec areaSetUnsafe = CommandSpec.builder()
                .description(Text.of("Set area as unsafe zone"))
                .executor(new SetUnsafeCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId"))
                )
                .build();

        CommandSpec areaRemove = CommandSpec.builder()
                .description(Text.of("Remove area"))
                .executor(new RemoveCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId"))
                )
                .build();

        CommandSpec areaDefineCuboid = CommandSpec.builder()
                .description(Text.of("Define cuboid area"))
                .executor(new DefineCuboidCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId")),
                        GenericArguments.string(Text.of("min")),
                        GenericArguments.string(Text.of("max"))
                )
                .build();

        CommandSpec areaDefineSphere = CommandSpec.builder()
                .description(Text.of("Define sphere area"))
                .executor(new DefineSphereCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId")),
                        GenericArguments.string(Text.of("center")),
                        GenericArguments.string(Text.of("fi"))
                )
                .build();

        CommandSpec areaRemoveHandler = CommandSpec.builder()
                .description(Text.of("Remove handler from area"))
                .executor(new RemoveHandlerCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId")),
                        GenericArguments.integer(Text.of("handlerId"))
                )
                .build();

        CommandSpec areaInfo = CommandSpec.builder()
                .description(Text.of("Info about area"))
                .executor(new InfoCommand())
                .arguments(
                        GenericArguments.string(Text.of("areaId"))
                )
                .build();


        CommandSpec areaCmd = CommandSpec.builder()
                .description(Text.of("Get stats for player"))
                .permission("squarekit.admin")
                .child(areaInfo, "info")
                .child(areaLoad, "load")
                .child(areaSave, "save")
                .child(areaClearHandlers, "clearHandlers")
                .child(areaRemoveHandler, "removeHandler")
                .child(areaAddHandler, "addHandler")
                .child(areaSetSafe, "setSafe")
                .child(areaSetUnsafe, "setUnsafe")
                .child(areaRemove, "remove")
                .child(areaDefineCuboid, "define-cuboid")
                .child(areaDefineSphere, "define-sphere")
                .build();

        Sponge.getCommandManager().register(this, areaCmd, "area");
    }

    public Path getConfigDir(){
        return configDir;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    private void saveConf(){
        if(Files.exists(getConfigDir())){
            try {
                Files.createDirectories(getConfigDir());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerSuffixes(){
        SuffixRegistry registry = getSuffixRegistry();
        registry.registerSuffix("AA", HealthRegen.class);
        registry.registerSuffix("AC", MaxHealth.class);
        registry.registerSuffix("BA", ManaRegen.class);
        registry.registerSuffix("BC", MaxMana.class);

        registry.registerSuffix("Aa", HoldingHealthRegen.class);
        registry.registerSuffix("Ac", HoldingMaxHealth.class);
        registry.registerSuffix("Ba", HoldingManaRegen.class);
        registry.registerSuffix("Bc", HoldingMaxMana.class);

        registry.registerSuffix("CA", PhysicalResistance.class);
        registry.registerSuffix("CB", MagicResistance.class);
        registry.registerSuffix("CE", KnockbackResistance.class);
        registry.registerSuffix("Ca", HoldingPhysicalResistance.class);
        registry.registerSuffix("Cb", HoldingMagicResistance.class);
        registry.registerSuffix("Ce", HoldingKnockbackResistance.class);

        registry.registerSuffix("DA", PhysicalDamage.class);
        registry.registerSuffix("Da", HoldingPhysicalDamage.class);

        registry.registerSuffix("EA", Speed.class);
        registry.registerSuffix("Ea", HoldingSpeed.class);

        registry.registerSuffix("MA", ArrowDamage.class);
        registry.registerSuffix("MB", EntanglingArrow.class);

        registry.registerSuffix("aN", Invisibility.class);
        registry.registerSuffix("bH", HoldingJumpBoost.class);
        registry.registerSuffix("bN", HoldingInvisibility.class);
        registry.registerSuffix("bP", HoldingNightVision.class);

        registry.registerSuffix("1A", HideShadow.class);
        registry.registerSuffix("1B", ShadowCloud.class);
        registry.registerSuffix("1C", MagicImbueWeapon.class);
        registry.registerSuffix("1D", SpiritsBurden.class);

        registry.registerSuffix("3A", Shelter.class);
        registry.registerSuffix("3B", IceGrowth.class);
        registry.registerSuffix("3C", IceRock.class);
        registry.registerSuffix("3D", Hook.class);
        registry.registerSuffix("3E", Rebound.class);
        registry.registerSuffix("3F", BeginnersLuck.class);
        registry.registerSuffix("3G", AlphaBugs.class);
        registry.registerSuffix("3H", FireShield.class);

        registry.registerSuffix("4G", FlameableLiquid.class);


        registry.registerSuffix("6A", ArrowGenerator.class);
        registry.registerSuffix("6B", Panic.class);
        registry.registerSuffix("6C", AstralVision.class);

    }

    private void registerListeners(){
        Sponge.getEventManager().registerListeners(this, new EventInterceptor());
        Sponge.getEventManager().registerListeners(this, new BattleInterceptor());
        Sponge.getEventManager().registerListeners(this, new WorldChangeListener());

        Sponge.getEventManager().registerListeners(this, new KitListener());
    }

    private void registerKits(){
            KitRegistry registry = getKitRegistry();

            MongoCollection collection = mongoDb.getCollection("kits");
            MongoCursor cursor = collection.find().iterator();

            while (cursor.hasNext()){
                Document kitDoc = (Document) cursor.next();
                registry.registerKit(kitDoc.getString("id"), Kit.fromDocument(kitDoc));
            }
    }

    public static SquareKit getInstance() {
        return instance;
    }

    public static Logger getLogger(){
        return logger;
    }

    public static SuffixRegistry getSuffixRegistry(){
        return SuffixRegistry.getInstance();
    }

    public static PlayersRegistry getPlayersRegistry(){
        return PlayersRegistry.getInstance();
    }

    public static KitRegistry getKitRegistry(){
        return KitRegistry.getInstance();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDb() {
        return mongoDb;
    }
}
