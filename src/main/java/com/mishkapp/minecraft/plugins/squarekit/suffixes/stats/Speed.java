package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class Speed extends Suffix {
    private float speed;

    public Speed(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            speed = (float) (-1 * (level - 31) * (100.0/32));
        } else {
            speed = (float) ((100.0/31) * level);
        }

        speed /= 100;
    }

    @Override
    public void register() {
        HashMap<Suffix, Float> adds = kitPlayer.getSpeedAdds();
        if(!adds.containsKey(this)){
            adds.put(this, speed);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("speed-suffix").replace("%SPEED%", Formatters.round.format(speed * 100));
    }
}
