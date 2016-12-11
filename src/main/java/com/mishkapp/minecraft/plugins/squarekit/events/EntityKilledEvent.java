package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class EntityKilledEvent extends KitEvent {
    private Entity killer;

    public EntityKilledEvent(KitPlayer player, Entity killer) {
        super(player);
        this.killer = killer;
    }

    public Entity getKiller() {
        return killer;
    }
}
