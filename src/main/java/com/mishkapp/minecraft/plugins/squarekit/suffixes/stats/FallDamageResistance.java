package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 03.02.17.
 */
public class FallDamageResistance extends Suffix {
    private double resistance;

    public FallDamageResistance(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length == 0){
            resistance = 0;
            return;
        }
        resistance = Double.parseDouble(args[0]);
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getFallDamageResistAdds();
        if(!adds.containsKey(this)){
            adds.put(this, resistance);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.fall-damage-resistance").replace("%RES%", FormatUtils.round(resistance * 100));
    }
}
