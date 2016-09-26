package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class ItemDamage extends Ticked {

    private int damage;

    public ItemDamage(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        if(level > 2047){
            damage = -1 * (level - 2047);
        } else {
            damage = level;
        }
    }

    @Override
    public void register(KitPlayer player) {

    }

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getAttackDamageAdds();
            if(isItemPresent(kitPlayer.getMcPlayer())){
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
        return TextColors.RED + "" + damage + " урона при ношении";
    }
}
