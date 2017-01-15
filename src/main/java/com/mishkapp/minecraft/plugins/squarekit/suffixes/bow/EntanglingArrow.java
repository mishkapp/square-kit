package com.mishkapp.minecraft.plugins.squarekit.suffixes.bow;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;

/**
 * Created by mishkapp on 06.10.2016.
 */
public class EntanglingArrow extends Suffix {

    private PotionEffect slow;

    private double time = 5;

    public EntanglingArrow(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            time = Double.parseDouble(args[0]);
        }

        slow = PotionEffect.builder()
                .potionType(PotionEffectTypes.SLOWNESS)
                .amplifier(2)
                .duration((int) (time * 20))
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

            applyEffects(entity, slow);
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("entangling-arrow-suffix")
                .replace("%TIME%", FormatUtils.unsignedTenth(time));
    }
}
