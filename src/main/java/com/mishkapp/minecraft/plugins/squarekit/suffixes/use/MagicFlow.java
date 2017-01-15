package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Random;

import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 13.12.2016.
 */
public class MagicFlow extends TargetedSuffix {
    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.DRIP_WATER)
            .offset(new Vector3d(0, 1, 0))
            .build();

    private Random random = new Random();

    private double duration = 7.0;
    private double speedReduction = -0.3;
    private double manaReduction = 0.25;

    public MagicFlow(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 3){
            duration = Double.parseDouble(args[3]);
        }
        if(args.length > 4){
            speedReduction = Double.parseDouble(args[4]);
        }
        if(args.length > 5){
            manaReduction = Double.parseDouble(args[5]);
        }
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected void onUse(Entity target) {
        if(!(target instanceof Player)){
            return;
        }
        KitPlayer targetPlayer = PlayersRegistry.getInstance().getPlayer(target.getUniqueId());

        double mana = targetPlayer.getCurrentMana();

        if(mana <= 5){
            targetPlayer.setCurrentMana(0);
            targetPlayer.getSpeedAdds().put(this, speedReduction);
            targetPlayer.updateStats();
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks((long) (duration * 20))
                    .execute(r -> {
                        targetPlayer.getSpeedAdds().remove(this);
                        targetPlayer.updateStats();
                    })
                    .submit(SquareKit.getInstance().getPlugin());
        } else {
            targetPlayer.setCurrentMana(mana - (targetPlayer.getMaxMana() * manaReduction));
            kitPlayer.setCurrentMana(kitPlayer.getCurrentMana() + (targetPlayer.getMaxMana() * manaReduction));
        }

        double distance = targetPlayer.getMcPlayer().getLocation().getPosition().distance(kitPlayer.getMcPlayer().getLocation().getPosition());

        for(double i = 0; i < distance; i += 0.5){
            final double p = i;
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks((long) i)
                    .execute(t -> {
                        double x0 = targetPlayer.getMcPlayer().getBoundingBox().get().getCenter().getX();
                        double y0 = targetPlayer.getMcPlayer().getBoundingBox().get().getCenter().getY();
                        double z0 = targetPlayer.getMcPlayer().getBoundingBox().get().getCenter().getZ();

                        double x = kitPlayer.getMcPlayer().getBoundingBox().get().getCenter().getX() - x0;
                        double y = kitPlayer.getMcPlayer().getBoundingBox().get().getCenter().getY() - y0;
                        double z = kitPlayer.getMcPlayer().getBoundingBox().get().getCenter().getZ() - z0;

                        double r = Math.sqrt((x * x) + (y * y) + (z * z));

                        double phi = Math.acos(z / r);
                        phi = Math.toDegrees(phi);

                        double theta = Math.acos(y / r);
                        theta = Math.toDegrees(theta);
                        theta = theta - 90.0;

                        if(x < 0){
                            phi = phi - 360.0;
                        } else {
                            phi = phi * (-1);
                        }

                        Vector3d point = targetPlayer.getMcPlayer().getLocation().getPosition().add(
                                p * -1 * sin(toRadians(phi)),
                                p * tan(toRadians(-1 * theta)),
                                p * cos(toRadians(phi))
                        );

                        targetPlayer.getMcPlayer().getWorld().spawnParticles(trailEffect,
                                point);
                    })
                    .submit(SquareKit.getInstance().getPlugin());
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("magic-flow-suffix")
                .replace("%SPEED_REDUCTION%", FormatUtils.round(speedReduction * 100))
                .replace("%MANA_REDUCTION%", FormatUtils.unsignedRound(manaReduction * 100))
                .replace("%DURATION%", FormatUtils.unsignedTenth(duration))
                + super.getLoreEntry();
    }
}
