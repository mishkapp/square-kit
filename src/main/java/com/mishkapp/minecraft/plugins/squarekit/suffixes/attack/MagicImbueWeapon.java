package com.mishkapp.minecraft.plugins.squarekit.suffixes.attack;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.Random;

import static org.spongepowered.api.event.cause.entity.damage.DamageTypes.MAGIC;

/**
 * Created by mishkapp on 13.10.2016.
 */
public class MagicImbueWeapon extends Suffix{
    private ParticleEffect effect;
    private Random rnd = new Random();
    private double damage;

    public MagicImbueWeapon(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        effect = ParticleEffect.builder()
                .type(ParticleTypes.WATER_SPLASH)
                .count(3)
                .offset(new Vector3d(0, 1, 0))
                .build();
        damage = level;
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
            attacked.damage(damage, DamageSource.builder().magical().bypassesArmor().type(MAGIC).build());
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
        return Messages.get("magic-imbue-weapon-suffix")
                .replace("%DAMAGE%", FormatUtils.round(damage));
    }
}
