package com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class WorldChangeListener {

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @First Player player){
        if (!player.hasPermission("squarekit.build")) {
            event.setCancelled(true);
        }
    }

}
