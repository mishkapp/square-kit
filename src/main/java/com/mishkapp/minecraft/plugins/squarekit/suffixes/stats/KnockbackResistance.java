package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 09.10.2016.
 */
public class KnockbackResistance extends Suffix {
    private double resistance;

    public KnockbackResistance(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            resistance = -2 * (level - 31);
        } else {
            resistance = level * 2;
        }

        resistance /= 100;
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getKnockbackResistsAdds();
        if(!adds.containsKey(this)){
            adds.put(this, resistance);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("knockback-resistance-suffix").replace("%RES%", FormatUtils.round(resistance * 100));
    }
}
