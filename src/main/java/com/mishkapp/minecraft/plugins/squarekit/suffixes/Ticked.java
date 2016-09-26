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
            return itemStack.equalTo(player.getHelmet().orElse(null)) ||
                    itemStack.equalTo(player.getChestplate().orElse(null)) ||
                    itemStack.equalTo(player.getLeggings().orElse(null)) ||
                    itemStack.equalTo(player.getBoots().orElse(null));

        } else if(ItemUtils.isWeapon(itemStack)) {
            return itemStack.equalTo(player.getItemInHand(MAIN_HAND).orElse(null));
        } else {
            return itemStack.equalTo(player.getItemInHand(MAIN_HAND).orElse(null))
                    || itemStack.equalTo(player.getItemInHand(OFF_HAND).orElse(null));
        }
    }
}
