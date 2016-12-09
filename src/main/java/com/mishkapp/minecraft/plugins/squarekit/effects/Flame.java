package com.mishkapp.minecraft.plugins.squarekit.effects;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

import java.util.Random;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class Flame extends Effect {

    private ParticleEffect pe = ParticleEffect.builder()
            .type(ParticleTypes.FLAME)
            .quantity(2)
            .build();

    private Random random = new Random();

    private int currentTick = 0;
    private int limitTick = 4;

    public Flame(KitPlayer kitPlayer, Suffix source, int level, long duration) {
        super(kitPlayer, source, level, duration);
    }

    @Override
    public void tick() {
        super.tick();
        if(currentTick < limitTick){
            currentTick += 1;
        } else {
            DamageSource source = EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).type(DamageTypes.FIRE).magical().bypassesArmor().build();
            kitPlayer.getMcPlayer().damage((level) * 1.5, source);
            drawEffect();
            currentTick = 0;
        }
    }

    private void drawEffect(){
        for(double i = 0; i < 2.0; i += random.nextDouble()){
            kitPlayer.getMcPlayer().getWorld().spawnParticles(
                    pe,
                    kitPlayer.getMcPlayer().getLocation().getPosition().add(
                            random.nextGaussian()/4,
                            i,
                            random.nextGaussian()/4
                    )
            );
        }
    }
}
