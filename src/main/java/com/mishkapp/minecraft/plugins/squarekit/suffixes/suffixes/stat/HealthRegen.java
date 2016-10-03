package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.stat;

import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Stat;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class HealthRegen extends Stat {
    private double healthRegen;

    public HealthRegen(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        healthRegen = level/(4096.0*4.0);

        if(level > 2047){
            healthRegen = -1 * (level - 2047) * (1.0/40.0);
        } else {
            healthRegen = (1.0/40.0) * level;
        }
    }

    @Override
    protected boolean isItemPresent() {
        return false;
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getHealthRegenAdds();
        if(!adds.containsKey(this)){
            adds.put(this, healthRegen);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-health-regen").replace("%REGEN%", Formatters.tenth.format(healthRegen * 4));
    }
}
