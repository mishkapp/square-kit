package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Attack extends Active {
    public Attack(ItemStack itemStack, int level) {
        super(itemStack, level);
    }
}
