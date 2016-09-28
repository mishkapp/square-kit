package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 27.04.2016.
 */
public abstract class Suffix {

    protected ItemStack itemStack;
    protected int level;

    public Suffix(ItemStack itemStack, int level) {
        this.itemStack = itemStack;
        this.level = level;
    }

    protected abstract boolean isItemPresent(Player player);

    public abstract void register(KitPlayer player);
    public abstract void handle(KitEvent event, KitPlayer kitPlayer);

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
