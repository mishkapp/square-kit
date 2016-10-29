package com.mishkapp.minecraft.plugins.squarekit.suffixes.bow;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 06.10.2016.
 */
public class EntanglingArrow extends Suffix {

    private int time;
    private PotionEffect effect;

    public EntanglingArrow(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        time = level * 5;
        effect = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .duration(time)
                .amplifier(1)
                .build();
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if (event instanceof ArrowHitEntityEvent) {
            if(!isWeaponInHand()){
                return;
            }
            Entity entity = ((ArrowHitEntityEvent) event).getTarget();

            List<PotionEffect> list = entity.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            list.add(effect);
            entity.offer(Keys.POTION_EFFECTS, list);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("entangling-arrow-suffix")
                .replace("%TIME%", FormatUtils.unsignedTenth(time/20.0));
    }
}
