package com.mishkapp.minecraft.plugins.squarekit.listeners;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerKilledByEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerKilledByPlayerEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerKilledEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.type.Exclude;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class KitListener {

    @Listener
    @Exclude({PlayerKilledByEntityEvent.class, PlayerKilledByPlayerEvent.class})
    public void onDeath(PlayerKilledEvent event){
        event.getPlayer().onDeath();
    }

    @Listener
    @Exclude({PlayerKilledByPlayerEvent.class})
    public void onDeath(PlayerKilledByEntityEvent event){
        event.getPlayer().onDeath();
    }

    @Listener
    public void onDeath(PlayerKilledByPlayerEvent event){
        KitPlayer killed = event.getPlayer();
        KitPlayer killer = event.getKiller();
        if(killer != killed){
            if(killed.getMcPlayer().getConnection().getAddress().getAddress().equals(killer.getMcPlayer().getConnection().getAddress().getAddress())){
                killer.getMcPlayer().sendMessage(_text(Messages.get("error-kill-from-same-ip")));
            } else {
                killer.onKill(killed);
            }
        }
        killed.onDeath();
    }
}
