package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.entity.projectile.arrow.Arrow;


/**
 * Created by Xpech on 07.08.2016.
 */
public class ArrowHitEntityEvent extends KitEvent{
    private Arrow arrow;
    private KitPlayer target;

    public ArrowHitEntityEvent(KitPlayer player, Arrow arrow, KitPlayer target) {
        super(player);
        this.arrow = arrow;
        this.target = target;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public KitPlayer getTarget() {
        return target;
    }
}
