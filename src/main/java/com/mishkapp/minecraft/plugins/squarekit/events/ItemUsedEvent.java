package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;

/**
 * Created by mishkapp on 11.05.2016.
 */
public class ItemUsedEvent extends KitEvent {
    public ItemUsedEvent(KitPlayer player) {
        super(player);
    }
}
