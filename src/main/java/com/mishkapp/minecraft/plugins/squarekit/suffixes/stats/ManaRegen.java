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
public class ManaRegen extends Suffix{
    private double manaRegen;

    public ManaRegen(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        if(level > 32){
            manaRegen = -1 * (level - 31) * 0.125;
        } else {
            manaRegen = 0.125 * level;
        }
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getManaRegenAdds();
        if(!adds.containsKey(this)){
            adds.put(this, manaRegen);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("mana-regen-suffix").replace("%REGEN%", Formatters.tenth.format(manaRegen * 4));
    }
}
