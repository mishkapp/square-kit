package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;

/**
 * Created by mishkapp on 14.10.2016.
 */
public class EntityCollideEntityEvent extends KitEvent {
    private Entity playersEntity;
    private Entity affectedEntity;

    public EntityCollideEntityEvent(KitPlayer player, Entity playersEntity, Entity affectedEntity) {
        super(player);
        this.playersEntity = playersEntity;
        this.affectedEntity = affectedEntity;
    }

    public Entity getPlayersEntity() {
        return playersEntity;
    }

    public void setPlayersEntity(Entity playersEntity) {
        this.playersEntity = playersEntity;
    }

    public Entity getAffectedEntity() {
        return affectedEntity;
    }

    public void setAffectedEntity(Entity affectedEntity) {
        this.affectedEntity = affectedEntity;
    }
}
