package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Utils;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

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
                        effects.add(effect);
                        break;
                    }
                }
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
        return TextColors.YELLOW + "" + effect.getType().getName() + " " + (effect.getAmplifier() - 1) + TextColors.WHITE + " при ношении";
    }
}
