package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;
import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class Obesity extends TargetedSuffix {

    private PotionEffect slow;

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
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected void onUse(Entity affected){
        if(!(affected instanceof Humanoid)){
            return;
        }

        Humanoid target = (Humanoid) affected;

        double targetFood = max(0, target.getFoodData().foodLevel().get());
        double targetSaturation = target.getFoodData().saturation().get();
        double playerFood = max(0, kitPlayer.getMcPlayer().getFoodData().foodLevel().get());
        double playerSaturation = kitPlayer.getMcPlayer().getFoodData().saturation().get();

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

            if(playerSaturation > 0){
                double delta = max(0, 20 - targetFood);
                if(delta <= playerSaturation){
                    targetFood += delta;
                    playerSaturation -= delta;
                } else if(targetFood != 20) {
                    targetFood += playerSaturation;
                    playerSaturation = 0;
                }
            }
        }

        if(playerFood > 0){
            targetSaturation += playerFood;
            playerFood = 0;
        }

        if(playerSaturation > 0){
            targetSaturation += playerSaturation;
            playerSaturation = 0;
        }

        kitPlayer.getMcPlayer().offer(Keys.FOOD_LEVEL, (int)playerFood);
        kitPlayer.getMcPlayer().offer(Keys.SATURATION, playerSaturation);
        target.offer(Keys.FOOD_LEVEL, (int)targetFood);
        target.offer(Keys.SATURATION, targetSaturation);

        applyEffects(target, slow);
        addEffect(target);
    }

    private void addEffect(Entity target){
        for(double i = 0; i < distance; i += 0.5){
            final double p = i;
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks((long) i)
                    .execute(t -> {
                        double x0 = target.getBoundingBox().get().getCenter().getX();
                        double y0 = target.getBoundingBox().get().getCenter().getY();
                        double z0 = target.getBoundingBox().get().getCenter().getZ();

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

                        Vector3d point = target.getLocation().getPosition().add(
                                p * -1 * sin(toRadians(phi)),
                                p * tan(toRadians(-1 * theta)),
                                p * cos(toRadians(phi))
                        );

                        Item item = createItem(point);

                        target.getWorld().spawnEntity(
                                item,
                                Cause.builder()
                                        .owner(SquareKit.getInstance())
                                        .build()
                        );
                    })
                    .submit(SquareKit.getInstance().getPlugin());
        }
    }

    private Item createItem(Vector3d vec){
        switch (SquareKit.random.nextInt(5)){
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
        result.offer(Keys.PICKUP_DELAY, (2 * 2) * 20);
        Sponge.getScheduler().createTaskBuilder()
                .execute(r -> result.remove())
                .delayTicks(2 * 20)
                .submit(SquareKit.getInstance().getPlugin());
        return result;
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("obesity-suffix")
                .replace("%DURATION%", FormatUtils.unsignedRound(duration))
                + super.getLoreEntry();
    }
}
