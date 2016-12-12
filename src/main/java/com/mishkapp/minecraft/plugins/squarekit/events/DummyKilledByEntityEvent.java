package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;

/**
 * Created by mishkapp on 12.12.2016.
 */
public class DummyKilledByEntityEvent extends DummyKilledEvent {
    protected Entity killerEntity;
    public DummyKilledByEntityEvent(KitPlayer player, Entity entity) {
        super(player);
        killerEntity = entity;
    }

    public Entity getKillerEntity() {
        return killerEntity;
    }
}
