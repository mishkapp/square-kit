package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedOnTargetEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 07.01.2017.
 */
public abstract class TargetedProjectileSuffix extends SpellSuffix {
    protected double hSpeed = 1.0;
    protected double vSpeed = 1.0;
    protected int liveTime = 1 * 20;

    private double distance = 30.0;

    protected List<Entity> launchedEntities = new ArrayList<>();

    private Random random = new Random();

    public TargetedProjectileSuffix(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            distance = Double.parseDouble(args[2]);
        }
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof EntityCollideEntityEvent){
            EntityCollideEntityEvent entityCollideEntityEvent = (EntityCollideEntityEvent)event;
            Entity playersEntity = entityCollideEntityEvent.getPlayersEntity();
            if(!launchedEntities.contains(playersEntity)){
                return;
            }
            onCollide(entityCollideEntityEvent.getAffectedEntity());
            launchedEntities.remove(playersEntity);
        }
        if(event instanceof ItemUsedOnTargetEvent){
            Player player = kitPlayer.getMcPlayer();
            Entity target = ((ItemUsedOnTargetEvent) event).getTarget();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(!(((ItemUsedOnTargetEvent) event).getTarget() instanceof Player)){
                return;
            }

            if(AreaRegistry.getInstance().isInSafeArea(player)){
                return;
            }

            if(((ItemUsedOnTargetEvent) event).getTarget().getLocation().getPosition().distance(player.getLocation().getPosition()) > distance){
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

            final Entity entity = prepareEntity();

            entity.setVelocity(thrustVec);

            entity.offer(Keys.HAS_GRAVITY, false);
            entity.setCreator(player.getUniqueId());
            launchedEntities.add(entity);

            world.spawnEntity(entity,
                    Cause.builder()
                            .owner(SquareKit.getInstance())
                            .build());

            onLaunch(entity, target);

            final Task effectTask = SpongeUtils.getTaskBuilder()
                    .intervalTicks(1)
                    .execute(o ->
                    {
                        addTrailEffect(entity);
                        correctThrust(entity, target);
                    })
                    .submit(SquareKit.getInstance());

            SpongeUtils.getTaskBuilder()
                    .delayTicks(liveTime)
                    .execute(o -> {
                        effectTask.cancel();
                        if(entity.isRemoved()){
                            return;
                        }
                        launchedEntities.remove(entity);
                        entity.remove();
                    })
                    .submit(SquareKit.getInstance());
        }
    }

    protected abstract Entity prepareEntity();

    protected abstract void onLaunch(Entity projectile, Entity target);

    protected abstract void onCollide(Entity affected);

    protected abstract void addTrailEffect(Entity entity);

    protected void correctThrust(Entity entity, Entity target){
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

}
