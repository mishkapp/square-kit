package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
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

import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;
import static java.lang.Math.min;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class Obesity extends TargetedProjectileSuffix {

    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.FLAME)
            .build();

    private Entity lastEntity = null;
    private PotionEffect slow;

    private Random random = new Random();

    private double duration = 10;

    public Obesity(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 3){
            duration = Double.parseDouble(args[3]);
        }

        slow = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .amplifier(1)
                .duration((int) (duration * 20))
                .build();

        hSpeed = 0.6;
        vSpeed = 0.6;
        liveTime = 7 * 20;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected Entity prepareEntity() {
        Item item = (Item) kitPlayer.getMcPlayer().getWorld().createEntity(EntityTypes.ITEM, kitPlayer.getMcPlayer().getLocation().getPosition().add(0, 1.75, 0));
        item.tryOffer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.FERMENTED_SPIDER_EYE, 1).createSnapshot());
        item.offer(Keys.PICKUP_DELAY, (1 * 2) * 20);
        item.offer(Keys.HAS_GRAVITY, false);
        return item;
    }

    @Override
    protected void onLaunch(Entity projectile, Entity target) {}

    @Override
    protected void onCollide(Entity affected){
        addCollideEffect(affected);
        if(!(affected instanceof Player)){
            return;
        }

        Player player = (Player) affected;

        int targetFood = player.getFoodData().foodLevel().get();
        int foodLevel = kitPlayer.getMcPlayer().getFoodData().foodLevel().get();

        player.offer(Keys.FOOD_LEVEL, min(20, targetFood + foodLevel));
        foodLevel -= targetFood;

        if(foodLevel > 0){
            player.offer(Keys.SATURATION, foodLevel + player.getFoodData().saturation().get());
        }

        applyEffects(player, slow);
    }

    @Override
    protected void addTrailEffect(Entity entity){
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
        return Messages.get("obesity-suffix")
                .replace("%DURATION%", FormatUtils.unsignedRound(duration))
                + super.getLoreEntry();
    }
}
