package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats.holding;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
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
public class HoldingManaRegen extends Suffix{
    private double manaRegen;

    public HoldingManaRegen(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        if(level > 32){
            manaRegen = -1 * (level - 31) * 0.025;
        } else {
            manaRegen = 0.025 * level;
        }
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getManaRegenAdds();
            if(isItemHolding()){
                adds.put(this, manaRegen);
            } else {
                adds.remove(this);
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("holding-mana-regen-suffix").replace("%REGEN%", FormatUtils.tenth(manaRegen * 4));
    }
}
