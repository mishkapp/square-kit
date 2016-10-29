package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class MaxHealth extends Suffix {
    private double health;

    public MaxHealth(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            health = -1 * (level - 31) * 2;
        } else {
            health = level * 2;
        }
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getMaxHealthAdds();
        if(!adds.containsKey(this)){
            adds.put(this, health);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("max-health-suffix").replace("%HEALTH%", FormatUtils.round(health));
    }
}
