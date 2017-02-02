package com.mishkapp.minecraft.plugins.squarekit.suffixes.attack;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.pureDamageKnock;

/**
 * Created by mishkapp on 03.02.17.
 */
public class PureDamageWeapon extends Suffix {
    private ParticleEffect effect;
    private Random rnd = new Random();

    private double damage = 1;

    public PureDamageWeapon(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            damage = Double.parseDouble(args[0]);
        }
        effect = ParticleEffect.builder()
                .type(ParticleTypes.REDSTONE_DUST)
                .quantity(3)
                .offset(new Vector3d(0, 1, 0))
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
            attacked.damage(damage, pureDamageKnock(kitPlayer.getMcPlayer()));
            addEffect(attacked);
        }
    }

    private void addEffect(Entity entity){
        World world = entity.getWorld();
        for(int i = 0; i < 10; i++){
            world.spawnParticles(effect,
                    entity.getLocation().getPosition().add(
                            rnd.nextGaussian() / 2,
                            rnd.nextGaussian() / 2,
                            rnd.nextGaussian() / 2
                    ));
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.pure-damage-weapon")
                .replace("%DAMAGE%", FormatUtils.round(damage));
    }
}
