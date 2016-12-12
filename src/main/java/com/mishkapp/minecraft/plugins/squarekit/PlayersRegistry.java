package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.events.PlayerUpdateRequestEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.TickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by mishkapp on 08.05.2016.
 */
public class PlayersRegistry {

    private static PlayersRegistry instance;

    private HashMap<UUID, KitPlayer> players = new HashMap<>();
    private HashMap<UUID, KitPlayer> inactive = new HashMap<>();

    private HashMap<UUID, KitPlayer> pending = new HashMap<>();

    private HashMap<UUID, Human> dummies = new HashMap<>();

    private HashMap<UUID, Ticker> tickers = new HashMap<>();

    public KitPlayer registerPlayer(UUID uuid){
        KitPlayer kitPlayer = KitPlayer.getKitPlayer(uuid);
        if(inactive.containsKey(uuid)){
            pending.put(uuid, inactive.remove(uuid));
        } else {
            pending.put(uuid, kitPlayer);
        }

        return kitPlayer;
    }

    public void initPlayer(Player player){
        UUID uuid = player.getUniqueId();
        if(pending.containsKey(uuid)){
            players.put(uuid, pending.remove(uuid));
            tickers.put(uuid, new Ticker(players.get(uuid)));
            players.get(uuid).init();
        }
    }

    public void unregisterPlayer(Player player){
        unregisterPlayer(player.getUniqueId());
    }

    public void unregisterPlayer(UUID uuid){
        if(players.containsKey(uuid)){
            inactive.put(uuid, players.remove(uuid));
            inactive.get(uuid).saveKitPlayer();
        }
        if(tickers.containsKey(uuid)){
            tickers.get(uuid).cancel();
            tickers.remove(uuid);
        }
    }


    public void prepareForUnregistrationPlayer(Player player) {
        final UUID uuid = player.getUniqueId();
        dummies.put(uuid, createDummy(player));
        Sponge.getScheduler().createTaskBuilder()
                .delayTicks(10 * 20)
                .execute(r -> {
                    Human dummy = dummies.remove(uuid);
                    if(dummy != null){
                        dummy.remove();
                    }
                    unregisterPlayer(uuid);
                })
                .submit(SquareKit.getInstance().getPlugin());
    }


    public void updateAllPlayers(){
        players.values().forEach(p -> Sponge.getEventManager().post(new PlayerUpdateRequestEvent(p)));
    }

    public void updatePlayer(UUID uuid){
       Sponge.getEventManager().post(new PlayerUpdateRequestEvent(players.get(uuid)));
    }

    public void tickAllPlayers(){
        players.values().forEach(KitPlayer::tick);
    }

    public void purge(){
        players = new HashMap<>();
    }

    public static PlayersRegistry getInstance(){
        if(instance == null){
            instance = new PlayersRegistry();
        }
        return instance;
    }

    public void savePlayers(){
        players.values().parallelStream().forEach(KitPlayer::saveKitPlayer);
    }

    public List<KitPlayer> getPlayers(){
        return new ArrayList<>(players.values());
    }

    public KitPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public KitPlayer getPlayer(UUID uuid) {
        if(players.containsKey(uuid)){
            return players.get(uuid);
        } else {
            return null;
        }
    }

    public Human createDummy(Player player){
        World world = player.getWorld();
        Human dummy = (Human) world.createEntity(EntityTypes.HUMAN, player.getLocation().getPosition());
        dummy.setHelmet(player.getHelmet().orElse(null));
        dummy.setChestplate(player.getChestplate().orElse(null));
        dummy.setLeggings(player.getLeggings().orElse(null));
        dummy.setBoots(player.getBoots().orElse(null));
        dummy.setCreator(player.getUniqueId());
        dummy.offer(Keys.DISPLAY_NAME, Text.of(player.getName()));
        dummy.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        dummy.offer(Keys.HEALTH, 10.0);

        world.spawnEntity(
                dummy,
                Cause.builder()
                        .owner(SquareKit.getInstance())
                        .build()
        );
        return dummy;
    }

    public boolean hasDummy(Human dummy) {
        return dummies.values().contains(dummy);
    }

    public boolean hasDummy(UUID uuid){
        return dummies.containsKey(uuid);
    }

    private class Ticker implements Runnable {
        private KitPlayer player;
        private Task task;

        int saveTick = 240;
        int currentTick = 0;

        public Ticker(KitPlayer player) {
            this.player = player;
            task = SpongeUtils.getTaskBuilder().execute(this)
                    .delayTicks(5)
                    .intervalTicks(5)
                    .name("SquareKit - Ticker")
                    .submit(SquareKit.getInstance());
        }

        @Override
        public void run() {
            TickEvent event = new TickEvent(player);
            Sponge.getEventManager().post(event);
            if(currentTick < saveTick){
                currentTick += 1;
            } else {
                player.saveKitPlayer();
                currentTick = 0;
            }
        }

        public void cancel(){
            task.cancel();
        }
    }
}
