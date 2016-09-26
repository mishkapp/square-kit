package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.Utils;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class ItemEffect extends Ticked {

    private PotionEffect effect;

    public ItemEffect(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        effect = Utils.getEffectByLevel(level, 1200);
    }

    @Override
    public void register(KitPlayer player) {

    }

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            Player player = event.getPlayer().getMcPlayer();
            List<PotionEffect> effects = player.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            if(isItemPresent(player)){
                for (PotionEffect e : effects) {
                    if(e.getType().equals(effect.getType()) && (e.getAmplifier() <= effect.getAmplifier())){
                        effects.remove(e);
                        break;
                    }
                }
                effects.add(effect);
            } else {
                for (PotionEffect e : effects) {
                    if(e.getType().equals(effect.getType()) && (e.getAmplifier() <= effect.getAmplifier())){
                        effects.remove(e);
                        break;
                    }
                }
            }
            player.offer(Keys.POTION_EFFECTS, effects);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-item-effect")
                .replace("%EFFECT%", effect.getType().getTranslation().get(Locale.ENGLISH) + " " + (effect.getAmplifier() + 1));
    }
}
