package com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class HoldingInvisibility extends HoldingEffectSuffix {
    public HoldingInvisibility(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level, PotionEffectTypes.INVISIBILITY);
    }
}
