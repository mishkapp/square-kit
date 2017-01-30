package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

/**
 * Created by mishkapp on 17.01.2017.
 */
public class EternalHunger extends Suffix {

    private double interval = 1;
    private Task hungerTask;

    public EternalHunger(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            interval = Double.parseDouble(args[0]);
        }
    }

    @Override
    public void register() {
        super.register();
        hungerTask = Sponge.getScheduler().createTaskBuilder()
                .interval((long) (interval * 1000), TimeUnit.MILLISECONDS)
                .execute(t -> {
                    int food = kitPlayer.getMcPlayer().getFoodData().foodLevel().get();
                    double saturation = kitPlayer.getMcPlayer().getFoodData().saturation().get();
                    if(saturation > 0){
                        kitPlayer.getMcPlayer().offer(Keys.SATURATION, max(0, saturation - 1));
                    } else {
                        kitPlayer.getMcPlayer().offer(Keys.FOOD_LEVEL, max(0, food - 1));
                    }
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    @Override
    public void unregister() {
        super.unregister();
        if(hungerTask != null){
            hungerTask.cancel();
        }
    }

    @Override
    public void handle(KitEvent event) {

    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.eternal-hunger")
                .replace("%INTERVAL%", FormatUtils.unsignedTenth(interval));
    }

    private class OrbitalFood {
        private Task task;
        private Item item;

        private double a;
        private double b;

        private double e;

        private double omega; //alpha
        private double i;     //beta
        private double w;     //gamma


        OrbitalFood(){

        }

        public void update(){
            double x0 = kitPlayer.getMcPlayer().getLocation().getX();
            double y0 = kitPlayer.getMcPlayer().getLocation().getY();
            double z0 = kitPlayer.getMcPlayer().getLocation().getZ();

            double newX;
            double newY;
            double newZ;

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
            result.offer(Keys.PICKUP_DELAY, Integer.MAX_VALUE);
            result.offer(Keys.HAS_GRAVITY, false);
            return result;
        }

    }
}
