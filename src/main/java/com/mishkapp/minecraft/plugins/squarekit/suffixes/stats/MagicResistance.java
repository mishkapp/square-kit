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
public class MagicResistance extends Suffix {
    private double magicResistance;

    public MagicResistance(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            magicResistance = -1 * (level - 31);
        } else {
            magicResistance = level;
        }

        magicResistance /= 100;
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getMagicResistAdds();
        if(!adds.containsKey(this)){
            adds.put(this, magicResistance);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("magic-resistance-suffix").replace("%MRES%", Formatters.round.format(magicResistance * 100));
    }
}
