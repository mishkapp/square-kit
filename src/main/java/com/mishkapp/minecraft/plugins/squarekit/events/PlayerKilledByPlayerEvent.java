package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;

/**
 * Created by mishkapp on 30.10.2016.
 */
public class PlayerKilledByPlayerEvent extends PlayerKilledByEntityEvent {
    protected KitPlayer killer;

    public PlayerKilledByPlayerEvent(KitPlayer player, KitPlayer killer) {
        super(player, killer.getMcPlayer());
        this.killer = killer;
    }

    public KitPlayer getKiller() {
        return killer;
    }
}
