package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.ItemUtils;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Ticked extends Passive {
    public Ticked(KitPlayer kitPlayer, ItemStack itemStack, int level) {
        super(kitPlayer, itemStack, level);
    }

    @Override
    protected boolean isItemPresent() {
        if(ItemUtils.isArmor(itemStack)){
            return isSimilar(itemStack, kitPlayer.getMcPlayer().getHelmet().orElse(null)) ||
                    isSimilar(itemStack, kitPlayer.getMcPlayer().getChestplate().orElse(null)) ||
                    isSimilar(itemStack, kitPlayer.getMcPlayer().getLeggings().orElse(null)) ||
                    isSimilar(itemStack, kitPlayer.getMcPlayer().getBoots().orElse(null));

        } else if(ItemUtils.isWeapon(itemStack)) {
            return isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null));
        } else {
            return isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null))
                    || isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(OFF_HAND).orElse(null));
        }
    }
}
