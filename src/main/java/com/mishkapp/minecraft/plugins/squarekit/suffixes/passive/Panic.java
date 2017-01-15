package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 31.10.2016.
 */
public class Panic extends Suffix {

    private double hpTreshold = 0.5;
    private double speed = 0.2;
    private double pRes = -0.1;
    private double mRes = -0.1;

    public Panic(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);

        if(args.length > 0){
            hpTreshold = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            speed = Double.parseDouble(args[1]);
        }
        if(args.length > 2){
            pRes = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            mRes = Double.parseDouble(args[3]);
        }

    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            double maxHealth = kitPlayer.getMaxHealth();
            double health = kitPlayer.getHealth();
            if((health/maxHealth) < hpTreshold){
                kitPlayer.getSpeedAdds().put(this, speed);
                kitPlayer.getPhysicalResistAdds().put(this, pRes);
                kitPlayer.getMagicResistAdds().put(this, mRes);
            } else {
                kitPlayer.getSpeedAdds().put(this, 0.0);
                kitPlayer.getPhysicalResistAdds().put(this, 0.0);
                kitPlayer.getMagicResistAdds().put(this, 0.0);
            }
            kitPlayer.updateStats();
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("panic-suffix")
                .replace("%HP%", FormatUtils.unsignedRound(hpTreshold * 100))
                .replace("%SPEED%", FormatUtils.round(speed * 100))
                .replace("%PRES%", FormatUtils.round(pRes * 100))
                .replace("%MRES%", FormatUtils.round(mRes * 100));
    }
}
