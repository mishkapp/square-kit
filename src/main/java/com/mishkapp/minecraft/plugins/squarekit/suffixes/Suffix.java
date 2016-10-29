package com.mishkapp.minecraft.plugins.squarekit.suffixes;

import com.mishkapp.minecraft.plugins.squarekit.utils.ItemUtils;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
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

    public void register() {}

    public void unregister() {
        kitPlayer.unregister(this);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Suffix)) return false;

        Suffix suffix = (Suffix) o;

        if (level != suffix.level) return false;
        if (!itemStack.equals(suffix.itemStack)) return false;
        return kitPlayer.equals(suffix.kitPlayer);

    }

    @Override
    public int hashCode() {
        int result = itemStack.hashCode();
        result = 31 * result + level;
        result = 31 * result + kitPlayer.hashCode();
        return result;
    }
}
