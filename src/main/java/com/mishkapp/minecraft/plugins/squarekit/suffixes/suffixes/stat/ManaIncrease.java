package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.stat;

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
public class ManaIncrease extends Stat {

    private double mana;

    public ManaIncrease(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 2047){
            mana = -1 * (level - 2047);
        } else {
            mana = level;
        }
    }

    @Override
    protected boolean isItemPresent() {
        return false;
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getMaxManaAds();
        if(!adds.containsKey(this)){
            adds.put(this, mana);
        }
    }

    @Override
    public void handle(KitEvent event) {

    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-mana-increase").replace("%MANA%", Integer.toString((int)mana));
    }
}
