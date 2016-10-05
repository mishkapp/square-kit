package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class PlayerAttackPlayerEvent extends KitEvent {
    private KitPlayer attacked;

    public PlayerAttackPlayerEvent(KitPlayer player, KitPlayer attacked) {
        super(player);
        this.attacked = attacked;
    }

    public KitPlayer getAttacked() {
        return attacked;
    }
}
