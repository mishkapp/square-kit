package com.mishkapp.minecraft.plugins.squarekit.suffixes.bow;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by mishkapp on 06.10.2016.
 */
public class ArrowDamage extends Suffix {

    private double damage;

    public ArrowDamage(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        damage = level;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if (event instanceof ArrowHitEntityEvent) {
            if(!isWeaponInHand()){
                return;
            }
            System.out.println("arrow = " + ((ArrowHitEntityEvent) event).getArrow());
            ArrowHitEntityEvent arrowHitEntityEvent = (ArrowHitEntityEvent)event;
            ((ArrowHitEntityEvent) event).getTarget().addPhysicalDamage(damage);
        }
    }

    @Override
    public String getLoreEntry() {
        return TextColors.RED + "" + damage + " к урону стрел при ношении";
    }

}
