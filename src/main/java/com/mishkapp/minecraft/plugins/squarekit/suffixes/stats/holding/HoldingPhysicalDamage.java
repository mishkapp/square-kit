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
 * Created by mishkapp on 08.10.2016.
 */
public class HoldingPhysicalDamage extends Suffix {
    private double damage;

    public HoldingPhysicalDamage(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            damage = -1 * (level - 31);
        } else {
            damage = level;
        }
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getPhysicalDamageAdds();
            if(isItemHolding()){
                adds.put(this, damage);
            } else {
                adds.remove(this);
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("holding-physical-damage-suffix").replace("%DAMAGE%", FormatUtils.round(damage));
    }

}
