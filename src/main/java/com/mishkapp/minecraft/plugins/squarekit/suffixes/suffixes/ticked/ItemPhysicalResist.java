package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created by mishkapp on 29.06.2016.
 */
public class ItemPhysicalResist extends Ticked {

    private double resist;

    public ItemPhysicalResist(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        if(level > 2047){
            resist = -1 * (level - 2047) * (100.0/2048.0);
        } else {
            resist = (100.0/2047.0) * level;
        }

        resist /= 100;
    }

    @Override
    public void register(KitPlayer player) {}

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Double> adds = kitPlayer.getPhysicalResistAdds();
            if(isItemPresent(kitPlayer.getMcPlayer())){
                if(!adds.containsKey(this)){
                    adds.put(this, resist);
                }
            } else {
                if(!adds.containsKey(this)){
                    adds.put(this, 0.0);
                }
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return TextColors.RED + "" + formatter.format(resist * 100) + "%" + TextColors.WHITE + " к физическому сопротивлению";
    }
}
