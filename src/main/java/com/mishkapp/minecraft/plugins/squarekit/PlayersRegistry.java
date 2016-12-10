package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.events.PlayerUpdateRequestEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.TickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

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
        UUID uuid = player.getUniqueId();
        if(players.containsKey(uuid)){
            inactive.put(uuid, players.remove(uuid));
            inactive.get(uuid).saveKitPlayer();
        }
        if(tickers.containsKey(uuid)){
            tickers.get(uuid).cancel();
            tickers.remove(uuid);
        }
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

    public KitPlayer getPlayer(UUID uuid) {
        if(players.containsKey(uuid)){
            return players.get(uuid);
        } else {
            return null;
        }
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
