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
public class HealthRegen extends Suffix {
    private double healthRegen;

    public HealthRegen(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        if(level > 32){
            healthRegen = -1 * (level - 31) * 0.125;
        } else {
            healthRegen = 0.125 * level;
        }
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
        return Messages.get("health-regen-suffix").replace("%REGEN%", Formatters.tenth.format(healthRegen * 4));
    }
}
