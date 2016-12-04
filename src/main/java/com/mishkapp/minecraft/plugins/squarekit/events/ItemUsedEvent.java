package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.data.type.HandType;

/**
 * Created by mishkapp on 11.05.2016.
 */
public class ItemUsedEvent extends KitEvent {
    private HandType handType;
    public ItemUsedEvent(KitPlayer player, HandType handType) {
        super(player);
        this.handType = handType;
    }

    public HandType getHandType() {
        return handType;
    }
}
