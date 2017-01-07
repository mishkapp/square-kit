package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
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

    public CholesterolExplosion(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        cooldown = 40 * 1000;
        manaCost = 0;
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();

            List<Entity> entities = player.getNearbyEntities(10).parallelStream().filter(e -> e instanceof Living && e != player).collect(Collectors.toList());

            int entitiesCount = entities.size();
            int foodLevel = player.getFoodData().foodLevel().get();
            player.offer(Keys.FOOD_LEVEL, 0);
            if(entitiesCount > 0){
                addEffect();
                entities.forEach(entity -> {
                    DamageSource ds = EntityDamageSource.builder()
                            .entity(kitPlayer.getMcPlayer())
                            .absolute()
                            .bypassesArmor()
                            .type(DamageTypes.PROJECTILE)
                            .build();
                    entity.damage((double)foodLevel / (double)entitiesCount, ds);
                    if(entity instanceof Player){
                        entity.offer(Keys.FOOD_LEVEL, Math.min(20, entity.get(Keys.FOOD_LEVEL).get() + (foodLevel/entitiesCount)));
                    }

                });
            }
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
