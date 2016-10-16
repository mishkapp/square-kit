package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class MaxMana extends Suffix {
    private double mana;

    public MaxMana(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            mana = -1 * (level - 31);
        } else {
            mana = level;
        }
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getMaxManaAdds();
        if(!adds.containsKey(this)){
            adds.put(this, mana);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("max-mana-suffix").replace("%MANA%", FormatUtils.round(mana));
    }
}
