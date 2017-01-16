package com.mishkapp.minecraft.plugins.squarekit;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.commands.*;
import com.mishkapp.minecraft.plugins.squarekit.commands.area.*;
import com.mishkapp.minecraft.plugins.squarekit.commands.bounty.AddBounty;
import com.mishkapp.minecraft.plugins.squarekit.commands.bounty.ListBounty;
import com.mishkapp.minecraft.plugins.squarekit.commands.reload.ReloadKitsCommand;
import com.mishkapp.minecraft.plugins.squarekit.commands.reload.ReloadMessagesCommand;
import com.mishkapp.minecraft.plugins.squarekit.commands.statspanel.*;
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
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.AntimagicBlade;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.MagicImbueWeapon;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.ShadowCloud;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.SpiritsBurden;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.bow.ArrowDamage;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.bow.DeadlyArrow;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.bow.EntanglingArrow;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.Invisibility;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingInvisibility;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingJumpBoost;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingNightVision;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.passive.AstralVision;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.passive.ItemGenerator;
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
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bson.Document;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
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
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfigPath;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private ConfigurationNode defaultConfig;

    @Inject
    private Game game;

    private boolean initialized = false;

    @Listener
    public void onInit(GameInitializationEvent event){
        instance = this;
        initConfig();
        initMongo();
        initSerializers();
        initCmds();
        initMessages();
    }

    @Listener
    public void onGameStarting(GameStartingServerEvent event){
        initConfigs();
        registerSuffixes();
        registerListeners();
        registerKits();
        BountyHandler.getInstance().init();
        WarpZonesRegistry.getInstance().init();
        initAreas();
        getPlayersRegistry().updateAllPlayers();

        Sponge.getScheduler().createTaskBuilder()
                .execute(r -> initialized = true)
                .async()
                .delay(3, TimeUnit.SECONDS)
                .submit(getPlugin());
    }

    @Listener
    public void onGameStopping(GameStartingServerEvent event){
        getPlayersRegistry().savePlayers();
    }

    private void initConfig(){
        configManager = HoconConfigurationLoader.builder().setPath(defaultConfigPath).build();
        try {
            defaultConfig = configManager.load();
            configManager.save(defaultConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMongo(){
        ConfigurationNode mongoNode = defaultConfig.getNode("mongodb");
        String host = mongoNode.getNode("host").getString("localhost");
        int port = mongoNode.getNode("port").getInt(27017);

        String user = mongoNode.getNode("user").getString("user");
        String authDatabase = mongoNode.getNode("authDatabase").getString("authDb");
        String password = mongoNode.getNode("password").getString("password");
        String database = mongoNode.getNode("database").getString("database");

        mongoClient = new MongoClient(
                new ServerAddress(host, port),
                Collections.singletonList(MongoCredential.createScramSha1Credential(user, authDatabase, password.toCharArray()))
        );
        mongoDb = mongoClient.getDatabase(database);
    }

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
        Messages.init();
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

        // /reload
        CommandSpec reloadMessages = CommandSpec.builder()
                .description(Text.of("Reload messages"))
                .executor(new ReloadMessagesCommand())
                .build();

        CommandSpec reloadKits = CommandSpec.builder()
                .description(Text.of("Reload kits"))
                .executor(new ReloadKitsCommand())
                .build();

        CommandSpec reloadCommand = CommandSpec.builder()
                .description(Text.of("Reload command"))
                .permission("squarekit.admin")
                .child(reloadMessages, "messages")
                .child(reloadKits, "kits")
                .build();

        Sponge.getCommandManager().register(this, reloadCommand, "reload");

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
                        GenericArguments.player(Text.of("player"))
                )
                .build();

        Sponge.getCommandManager().register(this, statsCmd, "stats");

        // /buildmode
        CommandSpec buildModeCmd = CommandSpec.builder()
                .description(Text.of("Toggle build mode"))
                .permission("squarekit.admin")
                .executor(new BuildModeCommand())
                .build();

        Sponge.getCommandManager().register(this, buildModeCmd, "buildmode");

        // /addmoney
        CommandSpec addMoneyCmd = CommandSpec.builder()
                .description(Text.of("Adds money to player"))
                .permission("squarekit.admin")
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.integer(Text.of("amount"))
                )
                .executor(new AddMoneyCommand())
                .build();

        Sponge.getCommandManager().register(this, addMoneyCmd, "addMoney");

        // /addexp
        CommandSpec addExpCmd = CommandSpec.builder()
                .description(Text.of("Adds experience to player"))
                .permission("squarekit.admin")
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.integer(Text.of("amount"))
                )
                .executor(new AddExpCommand())
                .build();

        Sponge.getCommandManager().register(this, addExpCmd, "addExp");

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

        // /Bounty
        CommandSpec bountyAdd = CommandSpec.builder()
                .description(Text.of("Add bounty to player"))
                .executor(new AddBounty())
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.integer(Text.of("bounty"))
                )
                .build();

        CommandSpec bountyList = CommandSpec.builder()
                .description(Text.of("List bounties"))
                .executor(new ListBounty())
                .build();

        CommandSpec bountyCmd = CommandSpec.builder()
                .description(Text.of("Bounty command"))
                .child(bountyAdd, "add")
                .child(bountyList, "list")
                .build();

        Sponge.getCommandManager().register(this, bountyCmd, "bounty");

        // /statspanel
        CommandSpec panelAdd = CommandSpec.builder()
                .description(Text.of("Open menu to add entries to panel"))
                .executor(new AddCommand())
                .build();

        CommandSpec entryAdd = CommandSpec.builder()
                .description(Text.of("Add entry to panel"))
                .executor(new AddEntryCommand())
                .arguments(
                        GenericArguments.string(Text.of("entry"))
                )
                .build();

        CommandSpec entryRemove = CommandSpec.builder()
                .description(Text.of("Remove entry from panel"))
                .executor(new RemoveEntryCommand())
                .arguments(
                        GenericArguments.integer(Text.of("index"))
                )
                .build();

        CommandSpec entryUp = CommandSpec.builder()
                .description(Text.of("Move entry up in panel"))
                .executor(new EntryUpCommand())
                .arguments(
                        GenericArguments.integer(Text.of("index"))
                )
                .build();

        CommandSpec entryDown = CommandSpec.builder()
                .description(Text.of("Move entry down in panel"))
                .executor(new EntryDownCommand())
                .arguments(
                        GenericArguments.integer(Text.of("index"))
                )
                .build();

        CommandSpec entrySetup = CommandSpec.builder()
                .description(Text.of("Setup panel"))
                .executor(new SetupCommand())
                .build();

        CommandSpec statsPanelCmd = CommandSpec.builder()
                .description(Text.of("Stats panel command"))
                .executor(new SetupCommand())
                .child(panelAdd, "add")
                .child(entryAdd, "addentry")
                .child(entryRemove, "remove")
                .child(entryUp, "up")
                .child(entryDown, "down")
                .child(entrySetup, "setup")
                .build();

        Sponge.getCommandManager().register(this, statsPanelCmd, "statspanel", "panel");


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

    public boolean isInitialized() {
        return initialized;
    }

    public PluginContainer getPlugin() {
        return plugin;
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
        registry.registerSuffix("CF", Evasion.class);

        registry.registerSuffix("Ca", HoldingPhysicalResistance.class);
        registry.registerSuffix("Cb", HoldingMagicResistance.class);
        registry.registerSuffix("Ce", HoldingKnockbackResistance.class);
        registry.registerSuffix("Cf", HoldingEvasion.class);

        registry.registerSuffix("DA", PhysicalDamage.class);
        registry.registerSuffix("Da", HoldingPhysicalDamage.class);

        registry.registerSuffix("EA", Speed.class);
        registry.registerSuffix("Ea", HoldingSpeed.class);

        registry.registerSuffix("MA", ArrowDamage.class);
        registry.registerSuffix("MB", EntanglingArrow.class);
        registry.registerSuffix("MC", DeadlyArrow.class);

        registry.registerSuffix("aN", Invisibility.class);
        registry.registerSuffix("bH", HoldingJumpBoost.class);
        registry.registerSuffix("bN", HoldingInvisibility.class);
        registry.registerSuffix("bP", HoldingNightVision.class);

        registry.registerSuffix("1A", HideShadow.class);
        registry.registerSuffix("1B", ShadowCloud.class);
        registry.registerSuffix("1C", MagicImbueWeapon.class);
        registry.registerSuffix("1D", SpiritsBurden.class);
        registry.registerSuffix("1E", AntimagicBlade.class);

        registry.registerSuffix("3A", Shelter.class);
        registry.registerSuffix("3B", IceGrowth.class);
        registry.registerSuffix("3C", IceRock.class);
        registry.registerSuffix("3D", Hook.class);
        registry.registerSuffix("3E", Rebound.class);
        registry.registerSuffix("3F", BeginnersLuck.class);
        registry.registerSuffix("3G", AlphaBugs.class);
        registry.registerSuffix("3H", FireShield.class);
        registry.registerSuffix("3I", CholesterolExplosion.class);
        registry.registerSuffix("3J", Obesity.class);
        registry.registerSuffix("3K", Dehydration.class);
        registry.registerSuffix("3L", Dummy.class);
        registry.registerSuffix("3M", MagicLeech.class);
        registry.registerSuffix("3N", MagicFlow.class);
        registry.registerSuffix("3O", Disguise.class);
        registry.registerSuffix("3P", LivingMine.class);
        registry.registerSuffix("3Q", Cloak.class);


        registry.registerSuffix("4G", FlameableLiquid.class);


        registry.registerSuffix("6A", ItemGenerator.class);
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
        getKitRegistry().init();
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
