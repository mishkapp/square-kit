package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.Triggered;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Utils;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowLaunchEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Triggered;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Xpech on 05.08.2016.
 */
public class ArrowEffect extends Triggered {
    //TODO: stub

    PotionEffect effect;

    public ArrowEffect(ItemStack itemStack, Integer level) {

        super(itemStack, level);
        effect = Utils.getEffectByLevel(level, 400);

    }

    @Override
    public void register(KitPlayer player) {}

    private void handleLaunch(ArrowLaunchEvent event) {
//
//        Arrow arrow = event.getArrow();
//        arrow.setMetadata("ArrowEffect", new PotionEffectType(SquareKit.getInstance(), effect));

    }

    private void handleHit(ArrowHitEntityEvent event) {
//
//        if (event.getTarget() instanceof LivingEntity) {
//
//            LivingEntity entity = (LivingEntity)event.getTarget();
//
//            if (entity instanceof Player)
//                if (((Player)entity).equals(event.getPlayer()))
//                    return;
//
//            Arrow arrow = event.getArrow();
//            if (arrow.hasMetadata("ArrowEffect")) {
//
//                PotionEffect arrowEffect = (PotionEffect) arrow.getMetadata("ArrowEffect").get(0).value();
//                if (effect.equals(arrowEffect)) {
//                    entity.addPotionEffect(effect);
//                }
//
//            }
//
//        }

    }

    @Override
    public void handle(KitEvent event, KitPlayer player) {

        if (event instanceof ArrowLaunchEvent) {
            handleLaunch((ArrowLaunchEvent) event);
        }

        if (event instanceof ArrowHitEntityEvent) {
            handleHit((ArrowHitEntityEvent) event);
        }

    }

    @Override
    public String getLoreEntry() {
        NumberFormat formatter = new DecimalFormat("#0.0");
        return TextColors.YELLOW + "" + effect.getType().getName() + " " + (effect.getAmplifier() - 1) + TextColors.WHITE + "на оппонента";
    }

}
