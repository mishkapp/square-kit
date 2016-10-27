package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 16.10.2016.
 */
public class Hook extends LaunchProjectileSuffix {
    private int duration;
    private double damage = 0.1;
    private PotionEffect potionEffect;

    public Hook(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level, EntityTypes.ENDER_PEARL);
        duration = 4 * 20;
        cooldown = 15 * 1000;
        manaCost = 40 - (level * 64.0/40);
        hSpeed = 3.0;
        vSpeed = 3.0;
        liveTime = (int)(0.5 * 20);

        potionEffect = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .duration(duration)
                .amplifier(4)
                .build();

        trailEffect = ParticleEffect.builder()
                .type(ParticleTypes.CRIT)
                .count(1)
                .offset(new Vector3d(0, 0, 0))
                .build();
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected void onCollide(Entity entity){
        List<PotionEffect> effects = entity.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
        effects.add(potionEffect);
        entity.offer(Keys.POTION_EFFECTS, effects);

        Vector3d velocity = kitPlayer.getMcPlayer().getLocation().getPosition()
                .sub(entity.getLocation().getPosition())
                .div(2.5);
        entity.setVelocity(velocity);

        DamageSource source = DamageSource.builder().type(DamageTypes.PROJECTILE).bypassesArmor().build();
        entity.damage(kitPlayer.getHealth() * damage, source);
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("hook-suffix")
                .replace("%DAMAGE%", FormatUtils.unsignedRound(damage * 100))
                + super.getLoreEntry();
    }
}
