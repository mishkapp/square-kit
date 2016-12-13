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
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.pureDamage;
import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class Dehydration extends UseSuffix {
    private double hSpeed = 0.6;
    private double vSpeed = 0.6;

    private double damage = 10.0;
    private int liveTime = 7 * 20;
    private int time = 10;
    private int duration = 20;

    private PotionEffect fatigue = PotionEffect.builder()
            .particles(true)
            .potionType(PotionEffectTypes.MINING_FATIGUE)
            .duration(duration * 20)
            .amplifier(1)
            .build();

    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.FLAME)
            .build();

    private Entity lastEntity = null;

    private Random random = new Random();

    public Dehydration(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 20 * 1000;
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
            lastEntity.remove();
        }
        if(event instanceof ItemUsedOnTargetEvent){
            Player player = kitPlayer.getMcPlayer();
            Entity target = ((ItemUsedOnTargetEvent) event).getTarget();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(AreaRegistry.getInstance().isInSafeArea(player)){
                return;
            }

            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();


            if(!(target instanceof Player)){
                return;
            }

            int targetFood = ((Player)target).getFoodData().foodLevel().get();
            target.offer(Keys.FOOD_LEVEL, 0);

            List<PotionEffect> effects = target.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            effects.add(fatigue);
            target.offer(Keys.POTION_EFFECTS, effects);

            target.damage(targetFood, pureDamage(player));

            int foodLevel = player.getFoodData().foodLevel().get();
            player.offer(Keys.FOOD_LEVEL, min(20, targetFood + foodLevel));
            foodLevel -= targetFood;

            if(foodLevel > 0){
                player.offer(Keys.SATURATION, foodLevel + player.getFoodData().saturation().get());
            }

            World world = player.getWorld();

            Vector3d spawnLoc = target.getLocation().getPosition();
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

            final Entity entity = player.getWorld().createEntity(EntityTypes.SNOWBALL, spawnLoc);

            entity.setVelocity(thrustVec);

            entity.offer(Keys.HAS_GRAVITY, false);
            entity.setCreator(target.getUniqueId());
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
                        correctThrust(entity, player);
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
        Item item = (Item) entity.getWorld().createEntity(EntityTypes.ITEM, entity.getLocation().getPosition());
        item.tryOffer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.FERMENTED_SPIDER_EYE, 1).createSnapshot());
        item.offer(Keys.PICKUP_DELAY, (1 * 2) * 20);
        item.offer(Keys.HAS_GRAVITY, false);

        entity.getWorld().spawnEntity(
                item,
                Cause.builder()
                        .owner(SquareKit.getInstance())
                        .build()
        );

        Sponge.getScheduler().createTaskBuilder()
                .execute(r -> item.remove())
                .delayTicks(1 * 20)
                .submit(SquareKit.getInstance().getPlugin());
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("dehydration-suffix")
                .replace("%DURATION%", FormatUtils.unsignedRound(duration))
                + super.getLoreEntry();
    }
}
