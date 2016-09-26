package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.ItemUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Ticked extends Passive {
    public Ticked(ItemStack itemStack, int level) {
        super(itemStack, level);
    }

    @Override
    protected boolean isItemPresent(Player player) {
        if(ItemUtils.isArmor(itemStack)){
            return itemStack.equals(player.getHelmet().orElse(null)) ||
                    itemStack.equals(player.getChestplate().orElse(null)) ||
                    itemStack.equals(player.getLeggings().orElse(null)) ||
                    itemStack.equals(player.getBoots().orElse(null));

        } else if(ItemUtils.isWeapon(itemStack)) {
            return itemStack.equals(player.getItemInHand(MAIN_HAND).orElse(null));
        } else {
            return itemStack.equals(player.getItemInHand(MAIN_HAND).orElse(null))
                    || itemStack.equals(player.getItemInHand(OFF_HAND).orElse(null));
        }
    }
}
