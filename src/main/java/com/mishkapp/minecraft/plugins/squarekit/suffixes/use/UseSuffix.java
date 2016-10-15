package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by mishkapp on 08.10.2016.
 */
public abstract class UseSuffix extends Suffix {
    protected double manaCost;
    protected double cooldown;
    protected long lastUse;

    public UseSuffix(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
    }

    protected boolean isCooldowned(KitPlayer kitPlayer){
        long delta = System.currentTimeMillis() - lastUse;
        if(delta < (cooldown * kitPlayer.getCooldownRate())){
            double time = ((cooldown * kitPlayer.getCooldownRate()) - delta)/1000.0;
            kitPlayer.getMcPlayer().sendMessage(
                    TextSerializers.FORMATTING_CODE.deserialize(
                            Messages.get("cooldown")
                                    .replace("%TIME%", Formatters.tenth.format(time)))
            );
            return false;
        } else {
            return true;
        }


    }
}
