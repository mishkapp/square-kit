package com.mishkapp.minecraft.plugins.squarekit.suffixes.effects.holding;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class HoldingNightVision extends HoldingEffectSuffix {
    public HoldingNightVision(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args, PotionEffectTypes.NIGHT_VISION);
    }
}
