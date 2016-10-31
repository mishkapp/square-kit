package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;

/**
 * Created by mishkapp on 30.10.2016.
 */
public class PlayerKilledByEntityEvent extends PlayerKilledEvent {
    protected Entity killerEntity;
    public PlayerKilledByEntityEvent(KitPlayer player, Entity entity) {
        super(player);
        killerEntity = entity;
    }

    public Entity getKillerEntity() {
        return killerEntity;
    }
}
