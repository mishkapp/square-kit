package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 06.10.2016.
 */
public class PhysicalResistance extends Suffix {
    private double resistance;

    public PhysicalResistance(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            resistance = -1 * (level - 31);
        } else {
            resistance = level;
        }

        resistance /= 100;
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getPhysicalResistAdds();
        if(!adds.containsKey(this)){
            adds.put(this, resistance);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("physical-resistance-suffix").replace("%PRES%", FormatUtils.round(resistance * 100));
    }
}
