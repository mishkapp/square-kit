package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.entity.Entity;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class PlayerAttackEntityEvent extends KitEvent {
    private Entity attacked;

    public PlayerAttackEntityEvent(KitPlayer player, Entity attacked) {
        super(player);
        this.attacked = attacked;
    }

    public Entity getAttacked() {
        return attacked;
    }
}
