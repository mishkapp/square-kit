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
 * Created by mishkapp on 11.05.2016.
 */
public class ItemHealthIncrease extends Ticked {

    private double health;

    public ItemHealthIncrease(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        if(level > 2047){
            health = -1 * (level - 2047);
        } else {
            health = level;
        }
    }

    @Override
    public void register(KitPlayer player) {
        player.getMaxHealthAdds().put(this, 0.0);
    }

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getMaxHealthAdds();
            double addition;
            if(isItemPresent(kitPlayer.getMcPlayer())){
                addition = health;
            } else {
                addition = 0.0;
            }
            double lastValue = adds.get(this);
            if(lastValue != addition){
                adds.put(this, addition);
                kitPlayer.updateStats();
            }
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-item-health-increase").replace("%HEALTH%", Formatters.round.format(health));
    }
}
