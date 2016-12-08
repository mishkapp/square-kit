package com.mishkapp.minecraft.plugins.squarekit.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class InventoryUtils {

    public static int countItems(Inventory inventory, ItemType type){
        AtomicInteger res = new AtomicInteger(0);
        inventory.spliterator().forEachRemaining(
                i -> {
                    if(i instanceof Slot){
                        ItemStack is = i.peek().get();
                        if(is.getItem().equals(type)){
                            res.addAndGet(is.getQuantity());
                        }
                    } else {
                        res.addAndGet(countItems(i, type));
                    }
                }
        );
        return res.get();
    }

    public static void addItem(Player player,ItemStack itemStack){
        player.getInventory().offer(itemStack);
    }
}
