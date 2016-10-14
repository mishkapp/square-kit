package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.spongepowered.api.item.ItemTypes.ARROW;

/**
 * Created by mishkapp on 09.10.2016.
 */
public class ArrowGenerator extends Suffix {
    private int regenCooldown;
    private int tickTimer;
    private final int ARROW_LIMIT = 60;

    public ArrowGenerator(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        regenCooldown = 63 - level;
        tickTimer = 0;
    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            if (tickTimer >= regenCooldown) {
                tickTimer = 0;
                CarriedInventory playerInventory = kitPlayer.getMcPlayer().getInventory();
                int arrowCount = 0;

                for (Inventory inv : playerInventory.slots()) {
                    Slot s = (Slot)inv;
                    ItemStack i = s.peek().orElse(null);
                    if(i == null){
                        continue;
                    }
                    if(i.getItem() == ARROW){
                        arrowCount += i.getQuantity();
                    }
                }

                if (arrowCount < ARROW_LIMIT) {
                    playerInventory.offer(ItemStack.of(ARROW, 1));
                }

            } else {
                tickTimer++;
            }
        }
    }

    @Override
    public String getLoreEntry() {
        NumberFormat formatter = new DecimalFormat("#0.0");
        return Messages.get("arrow-generator-suffix")
                .replace("%TIME%", Formatters.round.format(regenCooldown / 4));
    }
}