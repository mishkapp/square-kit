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
 * Created by mishkapp on 27.04.2016.
 */
public class ItemManaRegen extends Ticked {

    private float manaRegen;

    public ItemManaRegen(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        manaRegen = (float)level/(4096.0f*4.0f);
    }

    @Override
    public void register(KitPlayer player) {}

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            HashMap<Suffix, Float> adds = kitPlayer.getManaRegenAdds();
            if(isItemPresent(kitPlayer.getMcPlayer())){
                if(!adds.containsKey(this)){
                    adds.put(this, manaRegen);
                }
            } else {
                if(!adds.containsKey(this)){
                    adds.put(this, 0.0f);
                }
            }
        }
        kitPlayer.updateStats();
    }

    @Override
    public String getLoreEntry() {
        NumberFormat formatter = new DecimalFormat("#0.000");
        return TextColors.BLUE + "+" + formatter.format(manaRegen * 4) + TextColors.WHITE + " MP/сек";
    }
}
