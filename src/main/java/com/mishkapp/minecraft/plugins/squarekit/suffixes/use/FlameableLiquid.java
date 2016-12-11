package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.effects.Effect;
import com.mishkapp.minecraft.plugins.squarekit.effects.Flame;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedOnTargetEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class FlameableLiquid extends UseSuffix {
    private double hSpeed = 0.5;
    private double vSpeed = 0.5;

    private double damage = 10.0;
    private int liveTime = 7 * 20;
    private int time = 10;

    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.FLAME)
            .build();

    private Entity lastEntity = null;

    private Random random = new Random();

    public FlameableLiquid(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 3.5 * 1000;
        manaCost = 10 - (level * 64.0/10);
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

            final Entity entity = player.getWorld().createEntity(EntityTypes.FIREBALL, spawnLoc);

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
                    .execute(o ->
                    {
                        addTrailEffect(entity);
                        correctThrust(entity, ((ItemUsedOnTargetEvent) event).getTarget());
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
        addCollideEffect(affected);
        DamageSource source = EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).type(DamageTypes.PROJECTILE).magical().bypassesArmor().build();
        affected.damage(damage, source);

        if(!(affected instanceof Player)){
            return;
        }
        KitPlayer affectedPlayer = PlayersRegistry.getInstance().getPlayer(affected.getUniqueId());
        List<Effect> effects = affectedPlayer.getEffects();

        List<Effect> flames = effects.parallelStream().filter(e -> e instanceof Flame && e.isRunning()).collect(Collectors.toList());

        if(flames.size() > 0){
            Flame flame = (Flame) flames.get(0);
            flame.setRunning(false);
            affectedPlayer.addEffect(new Flame(affectedPlayer, this, Math.min(flame.getLevel() + 1, 5), time * 1000));
        } else {
            affectedPlayer.addEffect(new Flame(affectedPlayer, this, 1, time * 1000));
        }
    }

    private void correctThrust(Entity entity, Entity target){
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

        Vector3d thrustVec = new Vector3d(1, 1, 1).mul(
                hSpeed * -1 * sin(toRadians(phi)),
                vSpeed * tan(toRadians(-1 * theta)),
                hSpeed * cos(toRadians(phi))
        );
        entity.setVelocity(thrustVec);
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
        return Messages.get("flameable-liquid-suffix")
                .replace("%DAMAGE%", FormatUtils.unsignedRound(damage))
                .replace("%TIME%", FormatUtils.unsignedTenth(time))
                + super.getLoreEntry();
    }
}
