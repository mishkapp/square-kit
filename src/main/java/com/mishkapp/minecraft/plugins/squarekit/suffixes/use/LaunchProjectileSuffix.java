package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 15.10.2016.
 */
public abstract class LaunchProjectileSuffix extends UseSuffix {
    protected double hSpeed = 1.0;
    protected double vSpeed = 1.0;

    protected int liveTime = 1 * 20;
    protected ParticleEffect trailEffect;
    protected Entity lastEntity = null;

    protected EntityType entityType;

    private Random random = new Random();

    public LaunchProjectileSuffix(KitPlayer kitPlayer, ItemStack itemStack, Integer level, EntityType entityType) {
        super(kitPlayer, itemStack, level);
        this.entityType = entityType;

        cooldown = 4 * 1000;
        manaCost = 40 - (level * 64.0/40);
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof EntityCollideEntityEvent){
            EntityCollideEntityEvent entityCollideEntityEvent = (EntityCollideEntityEvent)event;
            Entity playersEntity = entityCollideEntityEvent.getPlayersEntity();
            if(playersEntity != lastEntity){
                return;
            }
            onCollide(entityCollideEntityEvent.getAffectedEntity());
            lastEntity.remove();
        }
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand()){
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

            World world = player.getWorld();

            Vector3d spawnLoc = player.getLocation().getPosition();
            Vector3d lookVec = player.getHeadRotation();
            Vector3d thrustVec = new Vector3d(1, 1, 1);

            spawnLoc = spawnLoc.add(
                    0,
                    1.75,
                    0
            );

            thrustVec = thrustVec.mul(
                    hSpeed * -1 * sin(toRadians(lookVec.getY())),
                    vSpeed * tan(toRadians(-1 * lookVec.getX())),
                    hSpeed * cos(toRadians(lookVec.getY()))
            );

            final Entity entity = player.getWorld().createEntity(entityType, spawnLoc);

            entity.setVelocity(thrustVec);

            entity.offer(Keys.HAS_GRAVITY, false);
            entity.setCreator(player.getUniqueId());
            lastEntity = entity;

            world.spawnEntity(entity,
                    Cause.builder()
                            .owner(SquareKit.getInstance())
                            .build());

            final Task effectTask = SpongeUtils.getTaskBuilder()
                    .intervalTicks(1)
                    .execute(o -> addTrailEffect(entity))
                    .submit(SquareKit.getInstance());

            SpongeUtils.getTaskBuilder()
                    .delayTicks(liveTime)
                    .execute(o -> {
                        effectTask.cancel();
                        if(entity.isRemoved()){
                            return;
                        }
                        entity.remove();
                    })
                    .submit(SquareKit.getInstance());
        }
    }

    protected abstract void onCollide(Entity affected);

    protected void addTrailEffect(Entity entity){
        if(trailEffect == null || entity == null || entity.isRemoved() || entity.isOnGround()){
            return;
        }
        entity.getWorld().spawnParticles(
                trailEffect,
                entity.getLocation().getPosition().add(random.nextGaussian()/4, random.nextGaussian()/4, random.nextGaussian()/4)
        );

    }

}
