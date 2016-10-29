package com.mishkapp.minecraft.plugins.squarekit.events;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.projectile.arrow.Arrow;


/**
 * Created by Xpech on 07.08.2016.
 */
public class ArrowHitEntityEvent extends KitEvent{
    private Arrow arrow;
    private Entity target;
    private double damageMultiplier;

    public ArrowHitEntityEvent(KitPlayer player, Arrow arrow, Entity target, double damageMultiplier) {
        super(player);
        this.arrow = arrow;
        this.target = target;
        this.damageMultiplier = damageMultiplier;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public Entity getTarget() {
        return target;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }
}
