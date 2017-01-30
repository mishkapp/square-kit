package com.mishkapp.minecraft.plugins.squarekit.suffixes.bow;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 06.10.2016.
 */
public class ArrowDamage extends Suffix {

    private int damage;

    public ArrowDamage(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            damage = Integer.parseInt(args[0]);
        }
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if (event instanceof ArrowHitEntityEvent) {
            if(!isWeaponInHand()){
                return;
            }
            ArrowHitEntityEvent arrowHitEntityEvent = (ArrowHitEntityEvent) event;
            arrowHitEntityEvent.getTarget().damage(
                    arrowHitEntityEvent.getDamageMultiplier() * damage,
                    EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).bypassesArmor().type(DamageTypes.PROJECTILE).build());
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.arrow-damage")
                .replace("%DAMAGE%", FormatUtils.round(damage));
    }

}
