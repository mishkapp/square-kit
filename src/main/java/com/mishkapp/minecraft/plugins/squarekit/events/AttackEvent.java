package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class AttackEvent extends KitEvent {
    private KitPlayer attacker;

    public AttackEvent(KitPlayer player, KitPlayer attacker) {
        super(player);
        this.attacker = attacker;
    }

    public KitPlayer getAttacker() {
        return attacker;
    }
}
