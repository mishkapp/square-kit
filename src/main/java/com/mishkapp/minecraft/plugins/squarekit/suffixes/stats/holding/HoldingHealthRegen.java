package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats.holding;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class HoldingHealthRegen extends Suffix{
    private double healthRegen;

    public HoldingHealthRegen(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        if(level > 32){
            healthRegen = -1 * (level - 31) * 0.025;
        } else {
            healthRegen = 0.025 * level;
        }
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getHealthRegenAdds();
            if(isItemHolding()){
                adds.put(this, healthRegen);
            } else {
                adds.put(this, 0.0);
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("holding-health-regen-suffix").replace("%REGEN%", FormatUtils.tenth(healthRegen * 4));
    }
}
