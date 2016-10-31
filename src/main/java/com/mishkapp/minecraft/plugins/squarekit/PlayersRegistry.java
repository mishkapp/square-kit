package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.events.PlayerUpdateRequestEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.TickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mishkapp on 08.05.2016.
 */
public class PlayersRegistry {

    private static PlayersRegistry instance;

    private HashMap<UUID, KitPlayer> players = new HashMap<>();

    private HashMap<UUID, Ticker> tickers = new HashMap<>();

    public KitPlayer registerPlayer(Player player){
        UUID uuid = player.getUniqueId();
        KitPlayer kitPlayer = KitPlayer.getKitPlayer(SquareKit.getInstance().getMongoDb(), player);
        players.put(uuid, kitPlayer);
        tickers.put(uuid, new Ticker(kitPlayer));
        return kitPlayer;
    }

    public void unregisterPlayer(Player player){
        UUID uuid = player.getUniqueId();
        getPlayer(player.getUniqueId()).saveKitPlayer(SquareKit.getInstance().getMongoDb());
        if(players.containsKey(uuid)){
            players.remove(uuid);
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

        public Ticker(KitPlayer player) {
            this.player = player;
            task = SpongeUtils.getTaskBuilder().execute(this)
                    .delayTicks(5)
                    .intervalTicks(5)
                    .name("SquareKit - Ticker: " + player.getMcPlayer().getName())
                    .submit(SquareKit.getInstance());
        }

        @Override
        public void run() {
            TickEvent event = new TickEvent(player);
            Sponge.getEventManager().post(event);
        }

        public void cancel(){
            task.cancel();
        }
    }
}
