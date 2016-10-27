package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
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
                                    .replace("%TIME%", FormatUtils.unsignedTenth(time)))
            );
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("use-suffix-trail")
                .replace("%COOLDOWN%", FormatUtils.unsignedTenth(cooldown/1000))
                .replace("%MANACOST%", FormatUtils.unsignedRound(manaCost));
    }
}
