package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.Random;

import static java.lang.Math.*;

/**
 * Created by mishkapp on 28.10.2016.
 */
public class Rebound extends UseSuffix{
    private Random random = new Random();
    private ParticleEffect particleEffect = ParticleEffect.builder()
            .quantity(3)
            .type(ParticleTypes.CLOUD)
            .build();

    public Rebound(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        manaCost = 10 - (level * 64.0/10);
        cooldown = 5 * 1000;
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            double currentMana = kitPlayer.getCurrentMana();

            if(currentMana < manaCost){
                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.get("nomana")));
                return;
            }
            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();

            kitPlayer.setCurrentMana(currentMana - manaCost);

            Vector3d lookVec = player.getRotation();

            Vector3d velocity = new Vector3d(
                    0.9 * sin(toRadians(lookVec.getY())),
                    0.5,
                    0.9 * -1 * cos(toRadians(lookVec.getY()))
            );
            addEffect();
            player.setVelocity(velocity);
        }
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
