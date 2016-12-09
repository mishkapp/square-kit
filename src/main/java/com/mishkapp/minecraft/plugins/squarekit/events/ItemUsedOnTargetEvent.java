package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.Entity;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class ItemUsedOnTargetEvent extends ItemUsedEvent {
    private Entity target;

    public ItemUsedOnTargetEvent(KitPlayer player, HandType handType, Entity target) {
        super(player, handType);
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
}
