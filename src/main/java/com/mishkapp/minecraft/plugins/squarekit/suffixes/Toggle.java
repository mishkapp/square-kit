package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Toggle extends Active {
    public Toggle(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
    }

    protected boolean isItemPresentInHand() {
        return isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null))
                || isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(OFF_HAND).orElse(null));
    }
}
