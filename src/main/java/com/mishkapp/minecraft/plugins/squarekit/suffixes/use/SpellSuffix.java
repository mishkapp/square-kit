package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by mishkapp on 08.10.2016.
 */
public abstract class SpellSuffix extends Suffix {
    protected double manaCost = 0;
    protected double cooldown = 0;
    protected long lastUse = 0;

    public SpellSuffix(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            manaCost = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            cooldown = Double.parseDouble(args[1]);
        }
    }

    protected boolean isCooldowned(KitPlayer kitPlayer){
        long delta = System.currentTimeMillis() - lastUse;
        if(delta < ((cooldown * 1000) * kitPlayer.getCooldownRate())){
            double time = (((cooldown * 1000) * kitPlayer.getCooldownRate()) - delta)/1000.0;
            kitPlayer.getMcPlayer().sendMessage(
                    TextSerializers.FORMATTING_CODE.deserialize(
                            Messages.get("alert.cooldown")
                                    .replace("%TIME%", FormatUtils.unsignedTenth(time)))
            );
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void handle(KitEvent event){
        if(event instanceof SuffixTickEvent){
            if(!isItemInHand()){
                return;
            }
            int barSize = 50;
            long delta = System.currentTimeMillis() - lastUse;
            double time = (((cooldown * 1000) * kitPlayer.getCooldownRate()) - delta)/1000.0;
            double ratio = time/(cooldown);
            ratio = Math.max(0.0, ratio);
            time = Math.max(0.0, time);
            int barChars = (int) (ratio * barSize);
            String cooldownBar = "[";
            cooldownBar += StringUtils.repeat('|', barSize - barChars);
            cooldownBar += StringUtils.repeat('.', barChars);
            cooldownBar += "] ";
            cooldownBar += "(" + FormatUtils.unsignedTenth(time) + "c.)";

            TextColor barColor;
            if(ratio <= 0.0){
                barColor = TextColors.BLUE;
            } else {
                barColor = TextColors.GOLD;
            }

            kitPlayer.getMcPlayer().sendMessage(ChatTypes.ACTION_BAR,
                    Text.builder(cooldownBar).color(barColor).build());
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.use-trail")
                .replace("%COOLDOWN%", FormatUtils.unsignedTenth(cooldown))
                .replace("%MANACOST%", FormatUtils.unsignedRound(manaCost));
    }
}
