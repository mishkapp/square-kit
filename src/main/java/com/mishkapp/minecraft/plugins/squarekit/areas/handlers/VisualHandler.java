package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class VisualHandler extends Handler {
    private ParticleEffect effect;

    public VisualHandler() {
        ParticleType type = ParticleTypes.FLAME;

        effect = ParticleEffect.builder()
                .quantity(1)
                .type(type)
                .build();
    }

    @Override
    public void tick(Area area) {
        area.getBoundPoints().forEach(p -> area.getWorld().spawnParticles(effect, p));
    }

    @Override
    public String serialize() {
        return "visual";
    }

    public static VisualHandler deserialize(String[] args){
        return new VisualHandler();
    }
}
