package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.InventoryUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Locale;

/**
 * Created by mishkapp on 09.10.2016.
 */
public class ItemGenerator extends Suffix {
    private int tickTimer;

    private double regenCooldown = 1;
    private int itemLimit = 60;
    private ItemType itemType = ItemTypes.APPLE;

    public ItemGenerator(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);

        if(args.length > 0){
            regenCooldown = Double.parseDouble(args[0]) * 4;
        }
        if(args.length > 1){
            itemLimit = Integer.parseInt(args[1]);
        }
        if(args.length > 2){
            itemType = Sponge.getGame().getRegistry().getType(ItemType.class, args[2]).orElse(ItemTypes.APPLE);
        }
    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            if (tickTimer >= regenCooldown) {
                tickTimer = 0;
                int itemCount = InventoryUtils.countItems(kitPlayer.getMcPlayer(), itemType);

                if (itemCount < itemLimit) {
                    InventoryUtils.addItem(kitPlayer.getMcPlayer(), ItemStack.of(itemType, 1));
                }

            } else {
                tickTimer++;
            }
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.item-generator")
                .replace("%TIME%", FormatUtils.unsignedRound(regenCooldown / 4))
                .replace("%ITEM%", itemType.getTranslation().get(new Locale("ru", "RU")))
                .replace("%LIMIT%", FormatUtils.unsignedRound(itemLimit));
    }
}
