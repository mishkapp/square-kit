package com.mishkapp.minecraft.plugins.squarekit.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class InventoryUtils {

    public static int countItems(Player player, ItemType type){
        PlayerInventory playerInventory = player.getInventory().query(PlayerInventory.class);
        int result = 0;
        result += countItems(playerInventory.getEquipment(), type);
        result += countItems(playerInventory.getHotbar(), type);
        result += countItems(playerInventory.getMain(), type);
        result += countItems(playerInventory.getOffhand(), type);
        return result;
    }

    public static int countItems(Inventory inventory, ItemType type){
        AtomicInteger res = new AtomicInteger(0);
        inventory.spliterator().forEachRemaining(
                i -> {
                    if(i instanceof Slot){
                        ItemStack is = i.peek().orElse(null);
                        if(is == null){
                            return;
                        }
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
        Hotbar hotbar = player.getInventory().query(Hotbar.class);
        addItem(hotbar, itemStack);
    }

    public static void addItem(Inventory inventory,ItemStack itemStack){
        AtomicInteger remain = new AtomicInteger(itemStack.getQuantity());
        inventory.iterator().forEachRemaining(h -> {
            if(remain.get() == 0){
                return;
            }
            Slot slot = (Slot) h;
            int stackSize = slot.getStackSize();
            if(slot.contains(itemStack.getItem()) && slot.getStackSize() < 64){
                if((stackSize + remain.get()) < 64){
                    itemStack.setQuantity(stackSize + remain.get());
                    slot.set(itemStack);
                    remain.set(0);
                } else {
                    slot.set(ItemStack.of(itemStack.getItem(), 64));
                    remain.set(remain.get() - (64 - stackSize));
                }
            }
            if(slot.getStackSize() == 0){
                slot.set(itemStack);
                remain.set(0);
            }
        });
    }
}
