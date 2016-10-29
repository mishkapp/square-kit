package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 08.10.2016.
 */
public class PhysicalDamage extends Suffix {
    private double damage;

    public PhysicalDamage(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        if(level > 32){
            damage = -1 * (level - 31);
        } else {
            damage = level;
        }
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getPhysicalDamageAdds();
        if(!adds.containsKey(this)){
            adds.put(this, damage);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("physical-damage-suffix").replace("%DAMAGE%", FormatUtils.round(damage));
    }
}
