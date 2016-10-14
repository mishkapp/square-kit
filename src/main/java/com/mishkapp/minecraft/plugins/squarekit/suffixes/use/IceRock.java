package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.*;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.toRadians;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 13.10.2016.
 */
public class IceRock extends UseSuffix {
    private int duration;
    private ParticleEffect particleEffect;
    private PotionEffect potionEffect;
    private Random random = new Random();

    private Entity lastEntity = null;


    public IceRock(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        duration = 10 * 20;
        cooldown = 4 * 1000;
        manaCost = 40 - (level * 64.0/40);

        potionEffect = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .duration(duration)
                .amplifier(2)
                .build();

        particleEffect = ParticleEffect.builder()
                .type(ParticleTypes.SNOWBALL)
                .count(16)
                .offset(new Vector3d(0, 0, 0))
                .build();
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof EntityCollideEntityEvent){
            EntityCollideEntityEvent entityCollideEntityEvent = (EntityCollideEntityEvent)event;
            Entity playersEntity = entityCollideEntityEvent.getPlayersEntity();
            if(playersEntity != lastEntity){
                return;
            }
            Entity affected = entityCollideEntityEvent.getAffectedEntity();
            List<PotionEffect> effects = affected.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            effects.add(potionEffect);
            affected.offer(Keys.POTION_EFFECTS, effects);

            if(!(affected instanceof Player)){
                return;
            }
            KitPlayer affectedPlayer = SquareKit.getPlayersRegistry().getPlayer(affected.getUniqueId());
            affectedPlayer.addMagicDamage(30);
            lastEntity.remove();
        }
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand()){
                return;
            }

            double currentMana = kitPlayer.getCurrentMana();

            if(currentMana < manaCost){
                player.sendMessage(Text.of(Messages.get("nomana")));
                return;
            }
            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();

            kitPlayer.setCurrentMana(currentMana - manaCost);

            World world = player.getWorld();

            Vector3d spawnLoc = player.getLocation().getPosition();
            Vector3d lookVec = player.getHeadRotation();
            Vector3d thrustVec = new Vector3d(1, 1, 1);

            spawnLoc = spawnLoc.add(
                    -1 * sin(toRadians(lookVec.getY())),
                    1.8,
                    cos(toRadians(lookVec.getY()))
            );

            thrustVec = thrustVec.mul(
                    1.3 * -1 * sin(toRadians(lookVec.getY())),
                    1.3 * cos(toRadians(lookVec.getX() + 90)),
                    1.3 * cos(toRadians(lookVec.getY()))
            );

            final Entity iceRock = world.createEntity(EntityTypes.SNOWBALL, spawnLoc);
            iceRock.setVelocity(thrustVec);
            iceRock.offer(Keys.HAS_GRAVITY, false);
            iceRock.setCreator(player.getUniqueId());
            lastEntity = iceRock;

            world.spawnEntity(iceRock,
                    Cause.builder()
                            .owner(SquareKit.getInstance())
                            .build());

            final Task effectTask = SpongeUtils.getTaskBuilder()
                    .intervalTicks(1)
                    .execute(o -> addEffect(iceRock))
                    .submit(SquareKit.getInstance());

            SpongeUtils.getTaskBuilder()
                    .delayTicks(2 * 20)
                    .execute(o -> {
                        effectTask.cancel();
                        if(iceRock == null || iceRock.isRemoved()){
                            return;
                        }
                        iceRock.remove();
                    })
                    .submit(SquareKit.getInstance());
        }
    }

    private void addEffect(Entity entity){
        if(entity == null || entity.isRemoved() || entity.isOnGround()){
            return;
        }
        entity.getWorld().spawnParticles(
                particleEffect,
                entity.getLocation().getPosition().add(random.nextGaussian()/4, random.nextGaussian()/4, random.nextGaussian()/4)
        );

    }

    @Override
    public String getLoreEntry() {
        return Messages.get("ice-growth-suffix")
                .replace("%DURATION%", Formatters.tenth.format(duration/20))
                .replace("%COOLDOWN%", Formatters.tenth.format(cooldown/1000))
                .replace("%MANACOST%", Formatters.round.format(manaCost));
    }
}
