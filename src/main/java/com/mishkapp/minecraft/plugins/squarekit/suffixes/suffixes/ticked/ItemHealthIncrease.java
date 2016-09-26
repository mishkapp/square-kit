package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

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
    public void register(KitPlayer player) {}

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getMaxHealthAdds();
            if(isItemPresent(kitPlayer.getMcPlayer())){
                if(!adds.containsKey(this)){
                    adds.put(this, health);
                }
            } else {
                if(!adds.containsKey(this)){
                    adds.put(this, 0.0);
                }
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        return TextColors.RED + "" + health + TextColors.WHITE + " к HP при ношении";
    }
}
