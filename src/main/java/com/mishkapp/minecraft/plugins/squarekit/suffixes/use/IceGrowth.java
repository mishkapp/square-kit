package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.World;

import java.util.Random;
import java.util.Set;

import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;
import static java.lang.Math.cos;
import static java.lang.Math.toRadians;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 13.10.2016.
 */
public class IceGrowth extends UseSuffix {

    private ParticleEffect particleEffect;
    private Random random = new Random();

    private PotionEffect slow;

    private double duration = 15.0;
    private double radius = 10.0;

    public IceGrowth(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            radius = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            duration = Double.parseDouble(args[3]);
        }

        slow = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .amplifier(1)
                .duration((int) (duration * 20))
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
    }

    @Override
    protected void onUse() {
        Player player = kitPlayer.getMcPlayer();
        AABB box = new AABB(
                player.getLocation().getPosition().add(-1 * radius, -2, -1 * radius),
                player.getLocation().getPosition().add(radius, 2, radius)
        );
        Vector3d loc = player.getLocation().getPosition();
        Set<Entity> entities = player.getWorld().getIntersectingEntities(box,
                entity ->
                        !entity.equals(player)
                                && entity instanceof Player
                                && entity.getLocation().getPosition().distance(loc) < radius);
        entities.forEach(entity -> applyEffects(entity, slow));
        addEffect();
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
        return Messages.get("suffix.ice-growth")
                .replace("%DURATION%", FormatUtils.unsignedRound(duration))
                .replace("%RADIUS%", FormatUtils.unsignedRound(radius))
                + super.getLoreEntry();
    }
}
