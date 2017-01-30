package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;

/**
 * Created by mishkapp on 17.01.2017.
 */
public class LazyBelly extends Suffix {
    private int treshold = 3;

    private PotionEffect jump = PotionEffect.builder()
            .amplifier(129)
            .duration(8)
            .potionType(PotionEffectTypes.JUMP_BOOST)
            .build();

    public LazyBelly(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            treshold = Integer.parseInt(args[0]);
        }
    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            if(kitPlayer.getMcPlayer().getFoodData().foodLevel().get() >= treshold){
                applyEffects(kitPlayer.getMcPlayer(), jump);
            }
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.lazy-belly")
                .replace("%TRESHOLD%", FormatUtils.unsignedRound(treshold));
    }
}
