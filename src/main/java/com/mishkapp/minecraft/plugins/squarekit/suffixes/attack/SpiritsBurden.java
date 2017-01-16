package com.mishkapp.minecraft.plugins.squarekit.suffixes.attack;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class SpiritsBurden extends Suffix {

    private PotionEffect poison;

    private PotionEffect slowness;

    private double manaCost = 3;
    private double duration = 15;

    public SpiritsBurden(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            manaCost = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            duration = Double.parseDouble(args[1]);
        }

        poison = PotionEffect.builder()
                .particles(true)
                .potionType(PotionEffectTypes.POISON)
                .duration((int) (duration * 20))
                .amplifier(1)
                .build();

        slowness = PotionEffect.builder()
                .particles(true)
                .potionType(PotionEffectTypes.SLOWNESS)
                .duration((int) (duration * 20))
                .amplifier(1)
                .build();
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

            applyEffects(entity, slowness, poison);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("spirits-burden-suffix")
                .replace("%TIME%", FormatUtils.unsignedRound(duration))
                .replace("%MANACOST%", FormatUtils.unsignedRound(manaCost));
    }
}
