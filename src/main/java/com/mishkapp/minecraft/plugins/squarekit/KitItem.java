package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

/**
 * Created by mishkapp on 12.10.2016.
 */
public class KitItem {
    private KitPlayer kitPlayer;
    private ItemStack itemStack;
    private String sufficesCode;
    private List<Suffix> suffices;

    public KitItem(KitPlayer kitPlayer, ItemStack itemStack, String sufficesCode) {
        this.kitPlayer = kitPlayer;
        this.itemStack = itemStack;
        this.sufficesCode = sufficesCode;
    }

    public KitPlayer getKitPlayer() {
        return kitPlayer;
    }

    public void setKitPlayer(KitPlayer kitPlayer) {
        this.kitPlayer = kitPlayer;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String getSufficesCode() {
        return sufficesCode;
    }

    public void setSufficesCode(String sufficesCode) {
        this.sufficesCode = sufficesCode;
    }

    public List<Suffix> getSuffices() {
        return suffices;
    }

    public void setSuffices(List<Suffix> suffices) {
        this.suffices = suffices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KitItem kitItem = (KitItem) o;

        if (!ItemUtils.isSimilar(itemStack, kitItem.itemStack)) return false;
        return sufficesCode != null ? sufficesCode.equals(kitItem.sufficesCode) : kitItem.sufficesCode == null;
    }

    @Override
    public String toString() {
        return "KitItem{" +
                "sufficesCode='" + sufficesCode + '\'' +
                ", itemStack=" + itemStack +
                '}';
    }
}
