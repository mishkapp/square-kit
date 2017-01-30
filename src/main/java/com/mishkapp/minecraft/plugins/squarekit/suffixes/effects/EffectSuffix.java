package com.mishkapp.minecraft.plugins.squarekit.suffixes.effects;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.translation.locale.NamedLocales;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 03.10.2016.
 */
public abstract class EffectSuffix extends Suffix {

    private int amplifier = 0;

    private PotionEffect effect;

    public EffectSuffix(KitPlayer kitPlayer, ItemStack itemStack, String[] args, PotionEffectType potionEffectType) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            amplifier = Integer.parseInt(args[0]);
        }
        effect = PotionEffect.builder()
                .duration(1200)
                .amplifier(amplifier)
                .potionType(potionEffectType)
                .particles(false)
                .build();
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            Player player = event.getPlayer().getMcPlayer();
            List<PotionEffect> effects = player.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            for (PotionEffect e : effects) {
                if(e.getType().equals(effect.getType()) && (e.getAmplifier() <= effect.getAmplifier())){
                    effects.remove(e);
                    break;
                }
            }
            effects.add(effect);
            player.offer(Keys.POTION_EFFECTS, effects);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.effect")
                .replace("%EFFECT%", effect.getType().getTranslation().get(NamedLocales.RUSSIAN) + " " + (amplifier + 1));
    }
}
