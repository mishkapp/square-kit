package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Passive extends Suffix{
    public Passive(KitPlayer kitPlayer, ItemStack itemStack, int level) {
        super(kitPlayer, itemStack, level);
    }
}
