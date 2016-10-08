package com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.spongepowered.api.effect.potion.PotionEffectTypes.JUMP_BOOST;

/**
 * Created by mishkapp on 08.10.2016.
 */
public class HoldingJumpBoost extends HoldingEffectSuffix {
    public HoldingJumpBoost(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level, JUMP_BOOST);
    }
}
