package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;

/**
 * Created by mishkapp on 16.10.2016.
 */
public class Hook extends LaunchProjectileSuffix {
    private PotionEffect slow;

    private double damage = 0.1;
    private double duration = 4.0;

    public Hook(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            damage = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            duration = Double.parseDouble(args[3]);
        }

        hSpeed = 3.0;
        vSpeed = 3.0;
        liveTime = (int)(0.75 * 20);

        slow = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .amplifier(4)
                .duration((int) (duration * 20))
                .build();

        trailEffect = ParticleEffect.builder()
                .type(ParticleTypes.CRITICAL_HIT)
                .quantity(1)
                .offset(new Vector3d(0, 0, 0))
                .build();
    }

    @Override
    protected Entity prepareEntity() {
        return kitPlayer.getMcPlayer().getWorld().createEntity(EntityTypes.ENDER_PEARL, kitPlayer.getMcPlayer().getLocation().getPosition().add(0, 1.75, 0));
    }

    @Override
    protected void onCollide(Entity entity){
        Vector3d a = kitPlayer.getMcPlayer().getLocation().getPosition();
        Vector3d b = entity.getLocation().getPosition();

        Vector3d velocity = a
                .sub(b)
                .add(0, b.distance(a) * 0.1, 0)
                .div(2.5);
        entity.setVelocity(velocity);

        DamageSource source = EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).type(DamageTypes.PROJECTILE).bypassesArmor().build();
        entity.damage(kitPlayer.getHealth() * damage, source);
        applyEffects(entity, slow);
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("hook-suffix")
                .replace("%DAMAGE%", FormatUtils.unsignedRound(damage * 100))
                .replace("%DURATION%", FormatUtils.unsignedRound(duration))
                + super.getLoreEntry();
    }
}