package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class TickEvent extends AbstractEvent {

    private KitPlayer player;

    public TickEvent(KitPlayer player) {
        this.player = player;
    }

    public KitPlayer getPlayer() {
        return player;
    }

    @Override
    public Cause getCause() {
        return null;
    }
}
