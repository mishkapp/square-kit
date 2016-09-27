package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class ItemSpeed extends Ticked {

    private float speed;

    public ItemSpeed(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        if(level > 2047){
            speed = (float) (-1 * (level - 2047) * (100.0/2048.0));
        } else {
            speed = (float) ((100.0/2047.0) * level);
        }

        speed /= 100;
    }

    @Override
    public void register(KitPlayer player) {
    }

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Float> adds = kitPlayer.getSpeedAdds();
            if(isItemPresent(kitPlayer.getMcPlayer())){
                    adds.put(this, speed);
            } else {
                    adds.put(this, 0.0f);
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-item-speed").replace("%SPEED%", Formatters.round.format(speed * 100));
    }
}
