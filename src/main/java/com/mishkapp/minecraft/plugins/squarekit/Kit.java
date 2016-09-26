package com.mishkapp.minecraft.plugins.squarekit;


import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.List;

/**
 * Created by mishkapp on 28.06.2016.
 */
@ConfigSerializable
public class Kit {

    @Setting
    private String name;
    @Setting
    private String description;
    @Setting
    private int price;
    @Setting
    private String permission;
    @Setting
    private ItemStack helmet;
    @Setting
    private ItemStack chestplate;
    @Setting
    private ItemStack leggings;
    @Setting
    private ItemStack boots;
    @Setting
    private ItemStack offhand;
    @Setting("other")
    private List<ItemStack> items;

    public Kit(){}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getPermission() {
        return permission;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public ItemStack getOffhand() {
        return offhand;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Kit{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", permission='" + permission + '\'' +
                ", helmet=" + helmet +
                ", chestplate=" + chestplate +
                ", leggings=" + leggings +
                ", boots=" + boots +
                ", offhand=" + offhand +
                ", items=" + items +
                '}';
    }

    public void applyToPlayer(Player player){
        CarriedInventory inventory = player.getInventory();
        inventory.clear();
        player.setHelmet(helmet);
        player.setChestplate(chestplate);
        player.setLeggings(leggings);
        player.setBoots(boots);
        items.forEach(inventory::offer);
        SquareKit.getPlayersRegistry().updatePlayer(player.getUniqueId());
    }
}
