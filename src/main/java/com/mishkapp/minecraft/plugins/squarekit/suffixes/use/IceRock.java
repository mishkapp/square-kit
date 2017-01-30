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
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.magicDamage;
import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.physicalDamage;
import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;

/**
 * Created by mishkapp on 13.10.2016.
 */
public class IceRock extends LaunchProjectileSuffix {
    private PotionEffect slow;

    private double duration = 10.0;
    private double pDamage = 0;
    private double mDamage = 30;

    public IceRock(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            duration = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            pDamage = Double.parseDouble(args[3]);
        }
        if(args.length > 4){
            mDamage = Double.parseDouble(args[4]);
        }

        hSpeed = 1.3;
        vSpeed = 1.3;

        slow = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .amplifier(2)
                .duration((int) (duration * 20))
                .build();

        trailEffect = ParticleEffect.builder()
                .type(ParticleTypes.SNOWBALL)
                .quantity(16)
                .offset(new Vector3d(0, 0, 0))
                .build();
    }

    @Override
    protected Entity prepareEntity() {
        return kitPlayer.getMcPlayer().getWorld().createEntity(EntityTypes.SNOWBALL, kitPlayer.getMcPlayer().getLocation().getPosition().add(0, 1.75, 0));
    }

    @Override
    protected void onCollide(Entity entity){
        entity.damage(mDamage, magicDamage(kitPlayer.getMcPlayer()));
        entity.damage(pDamage, physicalDamage(kitPlayer.getMcPlayer()));
        applyEffects(entity, slow);
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.ice-rock")
                .replace("%MDAMAGE%", FormatUtils.unsignedRound(mDamage))
                .replace("%PDAMAGE%", FormatUtils.unsignedRound(pDamage))
                .replace("%DURATION%", FormatUtils.unsignedRound(duration))
                + super.getLoreEntry();
    }
}
