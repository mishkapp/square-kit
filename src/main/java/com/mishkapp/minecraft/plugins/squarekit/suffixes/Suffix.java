package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Suffix {

    protected ItemStack itemStack;
    protected int level;
    protected KitPlayer kitPlayer;

    public Suffix(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        this.kitPlayer = kitPlayer;
        this.itemStack = itemStack;
        this.level = level;
    }

    protected abstract boolean isItemPresent();

    public abstract void register();

    public abstract void handle(KitEvent event);

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    protected boolean isSimilar(ItemStack a, ItemStack b){
        return a != null
                && b != null
                && a.getItem() == b.getItem()
                && a.get(Keys.ITEM_LORE).equals(b.get(Keys.ITEM_LORE));
    }

    public abstract String getLoreEntry();
}
