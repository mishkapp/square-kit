package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.magicDamage;
import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.physicalDamage;

/**
 * Created by mishkapp on 13.10.2016.
 */
public class IceRock extends LaunchProjectileSuffix {
    private int duration;
    private double pDamage = 15;
    private double mDamage = 15;
    private PotionEffect potionEffect;

    public IceRock(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level, EntityTypes.SNOWBALL);
        duration = 10 * 20;
        cooldown = 4 * 1000;
        manaCost = 40 - (level * 64.0/40);
        hSpeed = 1.3;
        vSpeed = 1.3;

        potionEffect = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .duration(duration)
                .amplifier(2)
                .build();

        trailEffect = ParticleEffect.builder()
                .type(ParticleTypes.SNOWBALL)
                .quantity(16)
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
        entity.damage(mDamage, magicDamage(kitPlayer.getMcPlayer()));
        entity.damage(pDamage, physicalDamage(kitPlayer.getMcPlayer()));
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("ice-rock-suffix")
                .replace("%MDAMAGE%", FormatUtils.unsignedRound(mDamage))
                .replace("%PDAMAGE%", FormatUtils.unsignedRound(pDamage))
                + super.getLoreEntry();
    }
}
