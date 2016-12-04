package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.*;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.cos;
import static java.lang.Math.toRadians;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 13.10.2016.
 */
public class IceGrowth extends UseSuffix {

    private int duration;
    private ParticleEffect particleEffect;
    private PotionEffect potionEffect;
    private Random random = new Random();

    private double radius = 10.0;

    public IceGrowth(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        duration = 15 * 20;
        cooldown = 30 * 1000;
        manaCost = 40 - (level * 64.0/40);

        potionEffect = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .duration(duration)
                .amplifier(1)
                .build();

        particleEffect = ParticleEffect.builder()
                .type(ParticleTypes.SNOW_SHOVEL)
                .quantity(1)
                .offset(new Vector3d(0, 1, 0))
                .build();
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

            AABB box = new AABB(
                    player.getLocation().getPosition().add(-1 * radius, -2, -1 * radius),
                    player.getLocation().getPosition().add(radius, 2, radius)
            );
            Vector3d loc = player.getLocation().getPosition();
            Set<Entity> entities = player.getWorld().getIntersectingEntities(box,
                    entity ->
                            !entity.equals(player)
                                    && entity instanceof Living
                                    && entity.getLocation().getPosition().distance(loc) < radius);
            entities.forEach(entity -> {
                List<PotionEffect> effects = entity.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
                effects.add(potionEffect);
                entity.offer(Keys.POTION_EFFECTS, effects);
                    });
            addEffect();
        }
    }

    private void addEffect(){
        final Player player = kitPlayer.getMcPlayer();
        final Vector3d loc = player.getLocation().getPosition();
        final World world = player.getWorld();
        for (int i = 0; i < radius; i++){
            final int i1 = i;
            SpongeUtils.getTaskBuilder()
                    .delayTicks(i)
                    .execute(o -> {
                        for(int j = 0; j < 360; j++){
                            world.spawnParticles(
                                    particleEffect,
                                    loc.add(-1 * sin(toRadians(j)) * i1, 0 , cos(toRadians(j)) * i1)
                            );
                        }
                    })
                    .submit(SquareKit.getInstance());
            world.spawnParticles(
                    particleEffect,
                    player.getLocation().getPosition().add(random.nextGaussian()/4, random.nextGaussian()/4, random.nextGaussian()/4)
            );
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("ice-growth-suffix")
                .replace("%DURATION%", FormatUtils.unsignedRound(duration/20))
                + super.getLoreEntry();
    }
}
