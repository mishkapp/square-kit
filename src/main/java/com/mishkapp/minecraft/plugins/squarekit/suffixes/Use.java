package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Use extends Active{

    protected long cooldown;
    protected long lastUse;

    private NumberFormat cooldownFormatter = new DecimalFormat("#0.00");

    public Use(KitPlayer kitPlayer, ItemStack itemStack, int level) {
        super(kitPlayer, itemStack, level);
    }

    protected boolean isCooldowned(){
        long delta = System.currentTimeMillis() - lastUse;
        if(delta < (cooldown * kitPlayer.getCooldownRate())){
            double time = ((cooldown * kitPlayer.getCooldownRate()) - delta)/1000.0;
            kitPlayer.getMcPlayer().sendMessage(Text.of(Messages.getMessage("cooldown").replace("%TIME%", cooldownFormatter.format(time))));
            return false;
        } else {
            return true;
        }
    }

    protected boolean isItemPresentInHand() {
        return itemStack.equalTo(kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null))
                || itemStack.equalTo(kitPlayer.getMcPlayer().getItemInHand(OFF_HAND).orElse(null));
    }

}
