package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class PlayerAttackedByEntity extends KitEvent{
    private Entity attacker;

    public PlayerAttackedByEntity(KitPlayer player, Entity attacker) {
        super(player);
        this.attacker = attacker;
    }

    public Entity getAttacker() {
        return attacker;
    }
}
