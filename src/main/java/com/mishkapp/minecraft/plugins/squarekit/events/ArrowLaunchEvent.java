package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.entity.projectile.arrow.Arrow;

/**
 * Created by Xpech on 05.08.2016.
 */
public class ArrowLaunchEvent extends KitEvent {
    private Arrow arrow;

    public ArrowLaunchEvent(KitPlayer player, Arrow arrow) {
        super(player);
        this.arrow = arrow;
    }

    public Arrow getArrow() {
        return arrow;
    }
}
