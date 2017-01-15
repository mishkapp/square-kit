package com.mishkapp.minecraft.plugins.squarekit.suffixes.stats;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class Speed extends Suffix {
    private double speed;

    public Speed(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length == 0){
            speed = 0;
            return;
        }
        speed = Double.parseDouble(args[0]);
    }

    @Override
    public void register() {
        HashMap<Suffix, Double> adds = kitPlayer.getSpeedAdds();
        if(!adds.containsKey(this)){
            adds.put(this, speed);
        }
    }

    @Override
    public void handle(KitEvent event) {}

    @Override
    public String getLoreEntry() {
        return Messages.get("speed-suffix").replace("%SPEED%", FormatUtils.round(speed * 100));
    }
}
