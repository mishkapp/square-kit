package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Attack extends Active {
    public Attack(KitPlayer kitPlayer, ItemStack itemStack, int level) {
        super(kitPlayer, itemStack, level);
    }
}
