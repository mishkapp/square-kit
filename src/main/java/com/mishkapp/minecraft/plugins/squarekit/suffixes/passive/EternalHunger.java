package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

/**
 * Created by mishkapp on 17.01.2017.
 */
public class EternalHunger extends Suffix {

    private double interval = 1;
    private Task task;

    public EternalHunger(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            interval = Double.parseDouble(args[0]);
        }
    }

    @Override
    public void register() {
        super.register();
        task = Sponge.getScheduler().createTaskBuilder()
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
        if(task != null){
            task.cancel();
        }
    }

    @Override
    public void handle(KitEvent event) {

    }

    @Override
    public String getLoreEntry() {
        return Messages.get("eternal-hunger")
                .replace("%INTERVAL%", FormatUtils.unsignedTenth(interval));
    }
}
