package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.*;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class CholesterolExplosion extends UseSuffix {
    private Random random = new Random();

    private double radius = 10.0;

    private int itemLiveTime = 4;

    public CholesterolExplosion(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            radius = Double.parseDouble(args[2]);
        }
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected void onUse() {
        Player player = kitPlayer.getMcPlayer();
        List<Entity> entities = player.getNearbyEntities(radius)
                .parallelStream()
                .filter(e -> e instanceof Humanoid && e != player)
                .collect(Collectors.toList());

        int entitiesCount = entities.size();
        double power = (player.getFoodData().foodLevel().get() + player.getFoodData().saturation().get()) / entities.size();
        player.offer(Keys.FOOD_LEVEL, 0);
        player.offer(Keys.SATURATION, 0.0);
        addEffect();
        if(entitiesCount > 0){
            entities.forEach(entity -> {
                entity.damage(power, DamageUtils.pureDamage(player));

                double targetFood = max(0, ((Humanoid)entity).getFoodData().foodLevel().get());
                double targetSaturation = ((Humanoid)entity).getFoodData().saturation().get();
                double playerFood = power;

                if(targetFood < 20){
                    if(playerFood > 0){
                        double delta = max(0, 20 - targetFood);
                        if(delta <= playerFood){
                            targetFood += delta;
                            playerFood -= delta;
                        } else {
                            targetFood += playerFood;
                            playerFood = 0;
                        }
                    }
                }

                if(playerFood > 0){
                    targetSaturation += playerFood;
                }

                entity.offer(Keys.FOOD_LEVEL, (int)targetFood);
                entity.offer(Keys.SATURATION, targetSaturation);
            });
        }
    }

    private void addEffect(){
        final Player player = kitPlayer.getMcPlayer();
        final Vector3d loc = player.getLocation().getPosition();
        final World world = player.getWorld();

        world.playSound(SoundTypes.ENTITY_PLAYER_BURP, loc, 5);

        for (int i = 0; i < 25; i++){
            double a = -1 * sin(random.nextDouble() * PI * 2);
            double b = cos(random.nextDouble() * PI * 2);

            Item item = createItem(loc.add(a, 1.75, b));

            item.setVelocity(new Vector3d(
                    0.8 * a,
                    0,
                    0.8 * b
            ));
            world.spawnEntity(
                    item,
                    Cause.builder()
                            .owner(SquareKit.getInstance())
                            .build()
            );

            Sponge.getScheduler().createTaskBuilder()
                    .execute(r -> item.remove())
                    .delayTicks(itemLiveTime * 20)
                    .submit(SquareKit.getInstance().getPlugin());
        }
    }

    private Item createItem(Vector3d vec){
        switch (random.nextInt(5)){
            case 0:
                return createItem(ItemTypes.FERMENTED_SPIDER_EYE, vec);
            case 1:
                return createItem(ItemTypes.ROTTEN_FLESH, vec);
            case 2:
                return createItem(ItemTypes.MUTTON, vec);
            case 3:
                return createItem(ItemTypes.PORKCHOP, vec);
            case 4:
                return createItem(ItemTypes.SPIDER_EYE, vec);
            default:
                return createItem(ItemTypes.SPIDER_EYE, vec);
        }
    }

    private Item createItem(ItemType itemType, Vector3d vec){
        Item result = (Item) kitPlayer.getMcPlayer().getWorld().createEntity(EntityTypes.ITEM, vec);
        result.tryOffer(Keys.REPRESENTED_ITEM, ItemStack.of(itemType, 1).createSnapshot());
        result.offer(Keys.PICKUP_DELAY, (itemLiveTime * 2) * 20);
        return result;
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("cholesterol-explosion-suffix")
                + super.getLoreEntry();
    }
}
