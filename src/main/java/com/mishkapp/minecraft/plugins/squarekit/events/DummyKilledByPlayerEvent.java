package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;

/**
 * Created by mishkapp on 12.12.2016.
 */
public class DummyKilledByPlayerEvent extends DummyKilledByEntityEvent {
    protected KitPlayer killer;

    public DummyKilledByPlayerEvent(KitPlayer player, KitPlayer killer) {
        super(player, killer.getMcPlayer());
        this.killer = killer;
    }

    public KitPlayer getKiller() {
        return killer;
    }
}
