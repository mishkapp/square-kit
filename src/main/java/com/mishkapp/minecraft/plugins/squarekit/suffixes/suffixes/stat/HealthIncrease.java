package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.stat;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Stat;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 30.06.2016.
 */
public class HealthIncrease extends Stat {

    private double health;

    public HealthIncrease(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        if(level > 2047){
            health = -1 * (level - 2047);
        } else {
            health = level;
        }
    }

    @Override
    protected boolean isItemPresent(Player player) {
        return false;
    }

    @Override
    public void register(KitPlayer player) {
        HashMap<Suffix, Double> adds = player.getMaxHealthAdds();
        if(!adds.containsKey(this)){
            adds.put(this, health);
        }
    }

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {

    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-health-increase").replace("%HEALTH%", Integer.toString((int)health));
    }
}