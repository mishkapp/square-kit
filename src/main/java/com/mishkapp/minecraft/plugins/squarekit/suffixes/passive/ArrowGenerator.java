package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.InventoryUtils;
import org.spongepowered.api.item.inventory.ItemStack;

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
                int arrowCount = InventoryUtils.countItems(kitPlayer.getMcPlayer(), ARROW);

                if (arrowCount < ARROW_LIMIT) {
                    InventoryUtils.addItem(kitPlayer.getMcPlayer(), ItemStack.of(ARROW, 1));
                }

            } else {
                tickTimer++;
            }
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("arrow-generator-suffix")
                .replace("%TIME%", FormatUtils.unsignedRound(regenCooldown / 4));
    }
}
