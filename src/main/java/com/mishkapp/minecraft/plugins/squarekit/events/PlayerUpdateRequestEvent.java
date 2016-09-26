package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * Created by mishkapp on 11.05.2016.
 */
public class PlayerUpdateRequestEvent extends AbstractEvent {

    private KitPlayer player;

    public PlayerUpdateRequestEvent(KitPlayer player) {
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
