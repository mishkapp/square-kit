package com.mishkapp.minecraft.plugins.squarekit.suffixes.attack;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class SpiritsBurden extends Suffix {

    private int duration = 15;

    private PotionEffect poison = PotionEffect.builder()
            .particles(true)
            .potionType(PotionEffectTypes.POISON)
            .duration(duration * 20)
            .amplifier(1)
            .build();

    private PotionEffect slowness = PotionEffect.builder()
            .particles(true)
            .potionType(PotionEffectTypes.SLOWNESS)
            .duration(duration * 20)
            .amplifier(1)
            .build();

    private double manaCost = 3;

    public SpiritsBurden(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof PlayerAttackEntityEvent){
            PlayerAttackEntityEvent attackEvent = (PlayerAttackEntityEvent)event;
            Player player = kitPlayer.getMcPlayer();
            if(!isWeaponInHand()){
                return;
            }

            double currentMana = kitPlayer.getCurrentMana();

            if(currentMana < manaCost){
//                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.get("nomana")));
                return;
            }
            kitPlayer.setCurrentMana(currentMana - manaCost);

            Entity entity = attackEvent.getAttacked();
            List<PotionEffect> effects = entity.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            effects.add(poison);
            effects.add(slowness);
            entity.offer(Keys.POTION_EFFECTS, effects);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("spirits-burden-suffix")
                .replace("%TIME%", FormatUtils.unsignedRound(duration))
                .replace("%MANACOST%", FormatUtils.unsignedRound(manaCost));
    }
}
