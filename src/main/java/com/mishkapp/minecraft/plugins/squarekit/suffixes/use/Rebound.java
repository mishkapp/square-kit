package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.Random;

import static java.lang.Math.*;

/**
 * Created by mishkapp on 28.10.2016.
 */
public class Rebound extends UseSuffix {
    private Random random = new Random();
    private ParticleEffect particleEffect = ParticleEffect.builder()
            .quantity(3)
            .type(ParticleTypes.CLOUD)
            .build();

    private double hVelocity = 0.9;
    private double vVelocity = 0.5;

    public Rebound(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            hVelocity = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            vVelocity = Double.parseDouble(args[3]);
        }

    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected void onUse() {
        Player player = kitPlayer.getMcPlayer();
        Vector3d lookVec = player.getRotation();

        Vector3d velocity = new Vector3d(
                hVelocity * sin(toRadians(lookVec.getY())),
                vVelocity,
                hVelocity * -1 * cos(toRadians(lookVec.getY()))
        );
        addEffect();
        player.setVelocity(velocity);
    }

    private void addEffect(){
        Player player = kitPlayer.getMcPlayer();
        World world = player.getWorld();
        for (int i = 0; i < 16; i++){
            world.spawnParticles(
                    particleEffect,
                    player.getLocation().getPosition().add(random.nextGaussian()/3 + 0.1, random.nextGaussian(), random.nextGaussian()/3 + 0.1)
            );
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("rebound-suffix")
                + super.getLoreEntry();
    }
}
