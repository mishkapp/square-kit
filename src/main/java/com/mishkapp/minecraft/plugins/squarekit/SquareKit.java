package com.mishkapp.minecraft.plugins.squarekit;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.mishkapp.minecraft.plugins.squarekit.commands.KitCommand;
import com.mishkapp.minecraft.plugins.squarekit.commands.KitsCommand;
import com.mishkapp.minecraft.plugins.squarekit.commands.UpdateCommand;
import com.mishkapp.minecraft.plugins.squarekit.listeners.KitListener;
import com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors.BattleInterceptor;
import com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors.EventInterceptor;
import com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors.WorldChangeListener;
import com.mishkapp.minecraft.plugins.squarekit.serializers.ItemStackSerializer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.active.HideShadow;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.MagicImbueWeapon;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.attack.ShadowCloud;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.bow.ArrowDamage;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.bow.EntanglingArrow;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.Invisibility;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingInvisibility;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingJumpBoost;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding.HoldingNightVision;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.passive.ArrowGenerator;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.stats.*;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.stats.holding.*;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.use.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        initSerializers();
        initCmds();
        initMessages();
        initMongo();
        saveConf();
        registerSuffixes();
        registerListeners();
        registerKits();
        getPlayersRegistry().updateAllPlayers();
    }

    private void initMongo(){
        mongoClient = new MongoClient(
                new ServerAddress("s7.squareland.ru", 27017),
                Collections.singletonList(MongoCredential.createScramSha1Credential("squarekit", "admin", "Pcy7F7Y9BBEgqzrA".toCharArray()))
        );
        mongoDb = mongoClient.getDatabase("squarekit");
    }

//    public void onServer() {
//        getSuffixRegistry().purge();
//        getPlayersRegistry().purge();
//        getKitRegistry().purge();
//    }

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
                .executor(new KitCommand())
                .arguments(
                        GenericArguments.string(Text.of("kitId"))
                )
                .build();

        Sponge.getCommandManager().register(this, kitCmd, "sqkit");

        // /kits
        CommandSpec kitsCmd = CommandSpec.builder()
                .description(Text.of("List all kits"))
                .executor(new KitsCommand())
                .build();

        Sponge.getCommandManager().register(this, kitsCmd, "sqkits");

        // /kits
        CommandSpec updateCmd = CommandSpec.builder()
                .description(Text.of("Update player"))
                .permission("squarekit.update")
                .executor(new UpdateCommand())
                .build();

        Sponge.getCommandManager().register(this, updateCmd, "squpdate");


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

        registry.registerSuffix("3A", Shelter.class);
        registry.registerSuffix("3B", IceGrowth.class);
        registry.registerSuffix("3C", IceRock.class);
        registry.registerSuffix("3D", Hook.class);
        registry.registerSuffix("3E", Rebound.class);

        registry.registerSuffix("6A", ArrowGenerator.class);

    }

    private void registerListeners(){
        Sponge.getEventManager().registerListeners(this, new EventInterceptor());
        Sponge.getEventManager().registerListeners(this, new BattleInterceptor());
        Sponge.getEventManager().registerListeners(this, new WorldChangeListener());

        Sponge.getEventManager().registerListeners(this, new KitListener());
    }

    private void registerKits(){
        try{
            KitRegistry registry = getKitRegistry();

            Path kitsPath = getConfigDir().resolve("kits");

            Asset asset = game.getAssetManager().getAsset(plugin, "kits").orElse(null);

            String zipPath = asset.getUrl().toString()
                    .replace("jar:file:", "")
                    .replaceFirst("!/.*", "");

            String pathInZip = asset.getUrl().toString()
                    .replaceFirst(".*jar!/", "");

            ZipFile zipFile = new ZipFile(zipPath);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            Set<String> kitNames = new HashSet<>();

            while(entries.hasMoreElements()){
                String fileName = entries.nextElement().getName();
                if(fileName.startsWith(pathInZip) && fileName.endsWith("conf")){
                    kitNames.add(fileName.replaceFirst(".*/", ""));
                }
            }

            Files.createDirectories(kitsPath);

            for(String s : kitNames){
                kitsPath.resolve(s).toFile().delete();
                Asset a = game.getAssetManager().getAsset(plugin, "kits/" + s).orElse(null);
                a.copyToDirectory(kitsPath);
            }

            File[] kitFiles = configDir.resolve("kits").toFile().listFiles((dir, name) -> name.endsWith(".conf"));

            for(File f : kitFiles){
                String kitId = f.getName().substring(0, f.getName().length() - 5);
                System.out.println(kitId);
                try{
                    ConfigurationNode cn = HoconConfigurationLoader.builder()
                            .setPath(f.toPath())
                            .build()
                            .load(ConfigurationOptions.defaults().setObjectMapperFactory(factory).setSerializers(serializers));
                    registry.registerKit(kitId, cn.getValue(TypeToken.of(Kit.class)));
                } catch (ObjectMappingException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
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
