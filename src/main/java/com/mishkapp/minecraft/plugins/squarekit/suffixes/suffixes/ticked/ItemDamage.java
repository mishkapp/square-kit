package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class ItemDamage extends Ticked {

    private int damage;

    public ItemDamage(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 2047){
            damage = -1 * (level - 2047);
        } else {
            damage = level;
        }
    }

    @Override
    public void register() {

    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getAttackDamageAdds();
            if(isItemPresent()){
                if(!adds.containsKey(this)){
                    adds.put(this, (double)damage);
                }
            } else {
                if(!adds.containsKey(this)){
                    adds.put(this, 0.0);
                }
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-item-damage").replace("%DAMAGE%", Integer.toString(damage));
    }
}
