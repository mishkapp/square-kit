package com.mishkapp.minecraft.plugins.squarekit;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.mishkapp.minecraft.plugins.squarekit.commands.KitCommand;
import com.mishkapp.minecraft.plugins.squarekit.commands.LoreCommand;
import com.mishkapp.minecraft.plugins.squarekit.serializers.ItemStackSerializer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.Triggered.ArrowDamage;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.Triggered.ArrowEffect;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.stat.HealthIncrease;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked.*;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use.Hook;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use.Shelter;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use.Thunderbolt;
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
import java.util.logging.Logger;

/**
 * Created by mishkapp on 27.04.2016.
 */
@Plugin(id = "squarekit", name = "Square Kit", version = "1.0")
public class SquareKit{

    private static SquareKit instance;

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
        saveConf();
        registerSuffixes();
        registerListeners();
        registerKits();
        getPlayersRegistry().updateAllPlayers();
    }

//    public void onServer() {
//        getSuffixRegistry().purge();
//        getPlayersRegistry().purge();
//        getKitRegistry().purge();
//    }

    private void initMessages(){
        Path messagesPath = getConfigDir().resolve("messages.conf");

        if(!messagesPath.toFile().exists()){
            Asset asset = game.getAssetManager().getAsset(plugin, "messages.conf").orElse(null);
            try {
                asset.copyToFile(messagesPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        // /lore
        CommandSpec loreCmd = CommandSpec.builder()
                .permission("squarekit.admin")
                .description(Text.of("Set first line of lore for item in hand"))
                .executor(new LoreCommand())
                .arguments(
                        GenericArguments.string(Text.of("lore"))
                )
                .build();
        Sponge.getCommandManager().register(this, loreCmd, "lore");

        // /kit
        CommandSpec kitCmd = CommandSpec.builder()
                .description(Text.of("Get specified kit"))
                .executor(new KitCommand())
                .arguments(
                        GenericArguments.string(Text.of("kitId"))
                )
                .build();

        Sponge.getCommandManager().register(this, kitCmd, "kit");


    }

    public Path getConfigDir(){
        return configDir;
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

        registry.registerSuffix(1, ItemManaRegen.class);
        registry.registerSuffix(2, Thunderbolt.class);
        registry.registerSuffix(3, ItemHealthIncrease.class);
        registry.registerSuffix(4, ItemPhysicalResist.class);
        registry.registerSuffix(5, ItemSpeed.class);
        registry.registerSuffix(6, ItemEffect.class);
        registry.registerSuffix(7, ItemDamage.class);
        registry.registerSuffix(8, HealthIncrease.class);
        registry.registerSuffix(9, ItemHealthRegen.class);
        registry.registerSuffix(10, Shelter.class);
        registry.registerSuffix(11, Hook.class);
        registry.registerSuffix(12, ItemArrowRegen.class);
        registry.registerSuffix(13, ArrowEffect.class);
        registry.registerSuffix(14, ArrowDamage.class);
    }

    private void registerListeners(){
        Sponge.getEventManager().registerListeners(this, new EventInterceptor());
    }

    private void registerKits(){
        System.out.println(1);
        KitRegistry registry = getKitRegistry();

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
            } catch (IOException | ObjectMappingException e){
                e.printStackTrace();
            }
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
}
