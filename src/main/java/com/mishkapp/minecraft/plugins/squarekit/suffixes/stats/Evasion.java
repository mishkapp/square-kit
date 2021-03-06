package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class Evasion extends Suffix{
    private double evasion;

    public Evasion(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length == 0){
            evasion = 0;
            return;
        }
        evasion = Double.parseDouble(args[0]);
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getEvasionAdds();
        if(!adds.containsKey(this)){
            adds.put(this, evasion);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.evasion").replace("%EVASION%", FormatUtils.round(evasion * 100));
    }
}
