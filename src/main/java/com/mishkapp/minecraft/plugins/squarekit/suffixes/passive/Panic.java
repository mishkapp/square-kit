package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 31.10.2016.
 */
public class Panic extends Suffix {

    public Panic(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            double maxHealth = kitPlayer.getMaxHealth();
            double health = kitPlayer.getHealth();
            if((health/maxHealth) < 0.5){
                kitPlayer.getSpeedAdds().put(this, 0.2);
                kitPlayer.getPhysicalResistAdds().put(this, -0.1);
                kitPlayer.getMagicResistAdds().put(this, -0.1);
            } else {
                kitPlayer.getSpeedAdds().put(this, 0.0);
                kitPlayer.getPhysicalResistAdds().put(this, 0.0);
                kitPlayer.getMagicResistAdds().put(this, 0.0);
            }
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("panic-suffix");
    }
}
