package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;

/**
 * Created by mishkapp on 30.10.2016.
 */
public class PlayerKilledEvent extends KitEvent {
    public PlayerKilledEvent(KitPlayer player) {
        super(player);
    }
}
