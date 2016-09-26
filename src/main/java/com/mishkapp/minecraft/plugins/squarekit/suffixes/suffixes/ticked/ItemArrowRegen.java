package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.ticked;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Ticked;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Xpech on 05.08.2016.
 */
public class ItemArrowRegen extends Ticked {


    private int regenCooldown;
    private int tickTimer;
    private final int itemLimit = 60;

    public ItemArrowRegen(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        regenCooldown = 2047 - level;
        tickTimer = 0;
    }

    @Override
    public void register(KitPlayer player) {}

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof SuffixTickEvent){
            //TODO: need to rework it
//            if (tickTimer >= regenCooldown) {
//                tickTimer = 0;
//                CarriedInventory playerInventory = kitPlayer.getMcPlayer().getInventory();
//                int arrownCount = 0;
//
//                for (ItemStack is : playerInventory.)) {
//                    if (is == null)
//                        continue;
//                    if (is.getType().equals(ItemTypes.ARROW)) {
//                        arrownCount += is.getAmount();
//                    }
//                }
//
//                if (arrownCount > itemLimit) {
//                    kitPlayer.getBukkitPlayer().getInventory().removeItem(new ItemStack(Material.ARROW, arrownCount - itemLimit));
//                }
//
//                if (arrownCount < itemLimit) {
//                    kitPlayer.getBukkitPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
//                }
//
//            } else {
//                tickTimer++;
//            }
        }
    }

    @Override
    public String getLoreEntry() {
        NumberFormat formatter = new DecimalFormat("#0.0");
        return TextColors.DARK_RED + "+ 1 стрела в " + formatter.format(regenCooldown / 4) + TextColors.WHITE + " сек";
    }

}