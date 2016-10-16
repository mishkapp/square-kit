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
public class HoldingMaxMana extends Suffix{
    private double mana;

    public HoldingMaxMana(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            mana = -1 * (level - 31);
        } else {
            mana = level;
        }
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getMaxManaAdds();
            if(isItemHolding()){
                adds.put(this, mana);
            } else {
                adds.put(this, 0.0);
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("holding-max-mana-suffix").replace("%MANA%", FormatUtils.round(mana));
    }
}
