package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.Triggered;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Triggered;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Xpech on 10.08.2016.
 */

public class ArrowDamage extends Triggered {

    private int damage;

    public ArrowDamage(ItemStack itemStack, Integer level) {

        super(itemStack, level);
        if(level > 2047){
            damage = -1 * (level - 2047);
        } else {
            damage = level;
        }

    }

    @Override
    public void register(KitPlayer player) {}

    @Override
    public void handle(KitEvent event, KitPlayer player) {
        //TODO: stub
//        if (event instanceof ArrowHitEntityEvent) {
//            ((ArrowHitEntityEvent)event).getTarget().addPhysicalDamage(damage);
//        }
    }

    @Override
    public String getLoreEntry() {
        return TextColors.RED + "" + damage + " к урону стрел при ношении";
    }

}
