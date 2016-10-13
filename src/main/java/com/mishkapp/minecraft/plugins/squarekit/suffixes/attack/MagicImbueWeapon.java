package com.mishkapp.minecraft.plugins.squarekit.suffixes.attack;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackPlayerEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Random;

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
                .type(ParticleTypes.WATER_DROP)
                .count(5)
                .offset(new Vector3d(0, 1, 0))
                .build();
        damage = level;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof PlayerAttackPlayerEvent){
            PlayerAttackPlayerEvent attackEvent = (PlayerAttackPlayerEvent)event;
            if(!isWeaponInHand()){
                return;
            }
            Player attacked = attackEvent.getAttacked().getMcPlayer();
            attacked.getWorld().spawnParticles(effect, attacked.getLocation().getPosition());
            attackEvent.getAttacked().addMagicDamage(damage);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("magic-imbue-weapon-suffix")
                .replace("%DAMAGE%", Formatters.round.format(damage));
    }
}
