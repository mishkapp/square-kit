package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedOnTargetEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.Random;

import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 13.12.2016.
 */
public class MagicLeech extends UseSuffix {
    private double hSpeed = 0.5;
    private double vSpeed = 0.5;
    private double correctionFactor = 0.15;

    private double damage = 10.0;
    private int liveTime = 5 * 20;
    private int time = 18;

    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.DRIP_WATER)
            .build();

    private Entity lastEntity = null;

    private Vector3d lastVelocity;

    private Random random = new Random();

    public MagicLeech(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 15 * 1000;
        manaCost = 0;
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
        if(event instanceof ItemUsedOnTargetEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
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
            Vector3d velocity = new Vector3d(1, 1, 1);

            spawnLoc = spawnLoc.add(
                    0,
                    1.75,
                    0
            );

            velocity = velocity.mul(
                    hSpeed * -1 * sin(toRadians(lookVec.getY())),
                    vSpeed * tan(toRadians(-1 * lookVec.getX())),
                    hSpeed * cos(toRadians(lookVec.getY()))
            );

            final Entity entity = player.getWorld().createEntity(EntityTypes.ENDERMITE, spawnLoc);

            entity.setVelocity(velocity);
            lastVelocity = velocity;

            entity.offer(Keys.HAS_GRAVITY, false);
            entity.setCreator(player.getUniqueId());
            lastEntity = entity;

            world.spawnEntity(entity,
                    Cause.builder()
                            .owner(SquareKit.getInstance())
                            .build());

            final Task effectTask = SpongeUtils.getTaskBuilder()
                    .intervalTicks(1)
                    .execute(o ->
                    {
                        addTrailEffect(entity);
                        correctVelocity(entity, ((ItemUsedOnTargetEvent) event).getTarget());
                    })
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

    private void onCollide(Entity affected){
        lastVelocity = null;
        addCollideEffect(affected);

        if(!(affected instanceof Player)){
            return;
        }
        KitPlayer affectedPlayer = PlayersRegistry.getInstance().getPlayer(affected.getUniqueId());

        Double manaRegen = affectedPlayer.getManaRegen();

        if(manaRegen > 0){
            manaRegen *= -1;
            affectedPlayer.getManaRegenAdds().put(this, manaRegen * 2);
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(time * 20)
                    .execute(r -> affectedPlayer.getManaRegenAdds().remove(this))
                    .submit(SquareKit.getInstance().getPlugin());
        }

        if(manaRegen <= 0){
            affectedPlayer.getHealthRegenAdds().put(this, manaRegen);
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(time * 20)
                    .execute(r -> affectedPlayer.getHealthRegenAdds().remove(this))
                    .submit(SquareKit.getInstance().getPlugin());
        }
    }

    private void correctVelocity(Entity entity, Entity target){
        if(lastVelocity == null){
            return;
        }
        double x0 = entity.getLocation().getX();
        double y0 = entity.getLocation().getY();
        double z0 = entity.getLocation().getZ();

        double x = target.getBoundingBox().get().getCenter().getX() - x0;
        double y = target.getBoundingBox().get().getCenter().getY() - y0;
        double z = target.getBoundingBox().get().getCenter().getZ() - z0;

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

        Vector3d newVelocity = new Vector3d(1, 1, 1).mul(
                hSpeed * -1 * sin(toRadians(phi)),
                vSpeed * tan(toRadians(-1 * theta)),
                hSpeed * cos(toRadians(phi))
        );

        newVelocity = lastVelocity.add(
                correctionFactor * (newVelocity.getX() - lastVelocity.getX()),
                correctionFactor * (newVelocity.getY() - lastVelocity.getY()),
                correctionFactor * (newVelocity.getZ() - lastVelocity.getZ())
        );

        entity.setVelocity(newVelocity);

        lastVelocity = newVelocity;
    }

    private void addTrailEffect(Entity entity){
        if(trailEffect == null || entity == null || entity.isRemoved() || entity.isOnGround()){
            return;
        }
        entity.getWorld().spawnParticles(
                trailEffect,
                entity.getLocation().getPosition().add(random.nextGaussian()/4, random.nextGaussian()/4, random.nextGaussian()/4)
        );
    }

    private void addCollideEffect(Entity entity){
        if(trailEffect == null || entity == null || entity.isRemoved() || entity.isOnGround()){
            return;
        }
        for(int i = 0; i < 25; i++)
            entity.getWorld().spawnParticles(
                    trailEffect,
                    entity.getLocation().getPosition().add(random.nextGaussian() * 1.2, random.nextGaussian() * 1.2, random.nextGaussian() * 1.2)
            );
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("magic-leech-suffix")
                .replace("%TIME%", FormatUtils.unsignedTenth(time))
                + super.getLoreEntry();
    }
}
