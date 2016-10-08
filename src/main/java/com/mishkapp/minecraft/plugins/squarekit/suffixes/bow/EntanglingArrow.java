package com.mishkapp.minecraft.plugins.squarekit.suffixes.bow;

import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
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
            Player player = ((ArrowHitEntityEvent) event).getTarget().getMcPlayer();

            List<PotionEffect> list = player.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            list.add(effect);
            player.offer(Keys.POTION_EFFECTS, list);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("entangling-arrow-suffix")
                .replace("%TIME%", Formatters.tenth.format(time/20.0));
    }
}