package com.mishkapp.minecraft.plugins.squarekit.listeners;

import com.mishkapp.minecraft.plugins.squarekit.TopStreakerBar;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerKilledByEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerKilledByPlayerEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerKilledEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.type.Exclude;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class KitListener {

    @Listener
    @Exclude({PlayerKilledByEntityEvent.class, PlayerKilledByPlayerEvent.class})
    public void onDeath(PlayerKilledEvent event){
        event.getPlayer().onDeath();
        TopStreakerBar.getInstance().update();
    }

    @Listener
    @Exclude({PlayerKilledByPlayerEvent.class})
    public void onDeath(PlayerKilledByEntityEvent event){
        event.getPlayer().onDeath();
        TopStreakerBar.getInstance().update();
    }

    @Listener
    public void onDeath(PlayerKilledByPlayerEvent event){
        KitPlayer killed = event.getPlayer();
        KitPlayer killer = event.getKiller();
        if(killer != killed){
            killer.onKill(killed);
        }
        killed.onDeath();
        TopStreakerBar.getInstance().update();
    }

}
