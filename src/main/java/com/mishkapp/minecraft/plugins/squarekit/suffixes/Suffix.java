package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.ItemUtils;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;

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

    public boolean isItemHolding(){
        if(ItemUtils.isArmor(itemStack)){
            return isSimilar(itemStack, kitPlayer.getMcPlayer().getHelmet().orElse(null)) ||
                    isSimilar(itemStack, kitPlayer.getMcPlayer().getChestplate().orElse(null)) ||
                    isSimilar(itemStack, kitPlayer.getMcPlayer().getLeggings().orElse(null)) ||
                    isSimilar(itemStack, kitPlayer.getMcPlayer().getBoots().orElse(null));

        } else if(ItemUtils.isWeapon(itemStack)) {
            return isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null));
        } else {
            return isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null))
                    || isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(OFF_HAND).orElse(null));
        }
    }

    protected boolean isWeaponInHand() {
        return isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null));
    }

    protected boolean isItemInHand() {
        return isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(MAIN_HAND).orElse(null))
                || isSimilar(itemStack, kitPlayer.getMcPlayer().getItemInHand(OFF_HAND).orElse(null));
    }

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
