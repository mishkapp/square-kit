package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedOnTargetEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Random;

import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 13.12.2016.
 */
public class MagicFlow extends UseSuffix {
    private double damage = 10.0;
    private int time = 7;

    private double speedReduction = -0.3;
    private double manaReduction = 0.25;

    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.DRIP_WATER)
            .offset(new Vector3d(0, 1, 0))
            .build();

    private Random random = new Random();

    public MagicFlow(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 7 * 1000;
        manaCost = 0;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof ItemUsedOnTargetEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(!(((ItemUsedOnTargetEvent) event).getTarget() instanceof Player)){
                return;
            }

            if(((ItemUsedOnTargetEvent) event).getTarget().getLocation().getPosition().distance(player.getLocation().getPosition()) > 10){
                return;
            }
            double currentMana = kitPlayer.getCurrentMana();

            if(currentMana < manaCost){
                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.get("nomana")));
                return;
            }
            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();

            kitPlayer.setCurrentMana(currentMana - manaCost);

            KitPlayer target = PlayersRegistry.getInstance().getPlayer(((ItemUsedOnTargetEvent) event).getTarget().getUniqueId());

            double mana = target.getCurrentMana();

            if(mana <= 5){
                target.setCurrentMana(0);
                target.getSpeedAdds().put(this, speedReduction);
                target.updateStats();
                Sponge.getScheduler().createTaskBuilder()
                        .delayTicks(time * 20)
                        .execute(r -> {
                            target.getSpeedAdds().remove(this);
                            target.updateStats();
                        })
                        .submit(SquareKit.getInstance().getPlugin());
            } else {
                target.setCurrentMana(mana - (target.getMaxMana() * manaReduction));
                kitPlayer.setCurrentMana(kitPlayer.getCurrentMana() + (target.getMaxMana() * manaReduction));
            }

            double distance = target.getMcPlayer().getLocation().getPosition().distance(kitPlayer.getMcPlayer().getLocation().getPosition());

            for(double i = 0; i < distance; i += 0.5){
                final double p = i;
                Sponge.getScheduler().createTaskBuilder()
                        .delayTicks((long) i)
                        .execute(t -> {
                            double x0 = target.getMcPlayer().getBoundingBox().get().getCenter().getX();
                            double y0 = target.getMcPlayer().getBoundingBox().get().getCenter().getY();
                            double z0 = target.getMcPlayer().getBoundingBox().get().getCenter().getZ();

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

                            Vector3d point = target.getMcPlayer().getLocation().getPosition().add(
                                    p * -1 * sin(toRadians(phi)),
                                    p * tan(toRadians(-1 * theta)),
                                    p * cos(toRadians(phi))
                            );

                            target.getMcPlayer().getWorld().spawnParticles(trailEffect,
                                    point);
                        })
                        .submit(SquareKit.getInstance().getPlugin());
            }
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("magic-flow-suffix")
                .replace("%MANAREDUCTION%", FormatUtils.unsignedRound(manaReduction * 100))
                .replace("%TIME%", FormatUtils.unsignedTenth(time))
                + super.getLoreEntry();
    }
}
