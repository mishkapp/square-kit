package com.mishkapp.minecraft.plugins.squarekit.suffixes.attack;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class ShadowCloud extends Suffix {

    private PotionEffect potionEffect;
    private ParticleEffect effect;
    private Random rnd = new Random();


    public ShadowCloud(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        effect = ParticleEffect.builder()
                .type(ParticleTypes.SMOKE_LARGE)
                .count(4)
                .offset(new Vector3d(0, 1, 0))
                .build();

        potionEffect = PotionEffect.builder()
                .potionType(PotionEffectTypes.BLINDNESS)
                .amplifier(1)
                .duration(2 * 20)
                .build();
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof PlayerAttackEntityEvent){
            PlayerAttackEntityEvent attackEvent = (PlayerAttackEntityEvent)event;
            if(!isWeaponInHand()){
                return;
            }
            Entity attacked = attackEvent.getAttacked();
            if(rnd.nextDouble() < 0.05){
                List<PotionEffect> list = attacked.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
                list.add(potionEffect);
                attacked.offer(Keys.POTION_EFFECTS, list);


                Task task = Sponge.getScheduler().createTaskBuilder()
                        .execute(o -> cloudEffect(attacked))
                        .intervalTicks(5)
                        .submit(SquareKit.getInstance());

                Sponge.getScheduler().createTaskBuilder()
                        .execute(o -> task.cancel())
                        .delay(2, TimeUnit.SECONDS)
                        .submit(SquareKit.getInstance());
            }
        }
    }

    private void cloudEffect(Entity entity){
        for(int i = 0; i < 4; i++){
            entity.getWorld().spawnParticles(
                    effect,
                    entity.getLocation().getPosition().add(rnd.nextGaussian()/4, rnd.nextGaussian()/4, rnd.nextGaussian()/4),
                    25);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("shadow-cloud-suffix");
    }
}
