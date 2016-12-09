package com.mishkapp.minecraft.plugins.squarekit;


import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.ItemUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.MongoUtils;
import org.bson.Document;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 28.06.2016.
 */
public class Kit {

    private String id;
    private String name;
    private String description;
    private int price;
    private String permission;
    private int minLevel;
    private ItemStack menuItem;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack offhand;
    private List<ItemStack> items = new ArrayList<>();

    private Kit(){}

    public ItemStack getMenuItem(){
        return menuItem;
    }

    public String getId() {
        return id;
    }

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

    public int getMinLevel() {
        return minLevel;
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

    public void applyToPlayer(KitPlayer kitPlayer){
        Player player = kitPlayer.getMcPlayer();
        CarriedInventory inventory = player.getInventory();
        inventory.clear();
        player.offer(Keys.POTION_EFFECTS, new ArrayList<>());
        player.setHelmet(helmet);
        player.setChestplate(chestplate);
        player.setLeggings(leggings);
        player.setBoots(boots);
        List<ItemStack> other = new ArrayList<>();
        items.forEach(o -> other.add(o.copy()));
        Hotbar hotbar = inventory.query(Hotbar.class);
        for(int i = 0; i < other.size(); i++){
            hotbar.set(new SlotIndex(i), other.get(i));
        }
        hotbar.setSelectedSlotIndex(0);
        kitPlayer.setCurrentKit(id);
        kitPlayer.forceUpdate();
    }

    public static Kit fromDocument(Document document){
        Kit kit = new Kit();
        kit.id = document.getString("id");
        kit.name = document.getString("name");
        kit.description = document.getString("description");
        kit.price = document.getInteger("price");
        kit.permission = document.getString("permission");

        if(document.containsKey("minLevel")){
            kit.minLevel = document.getInteger("minLevel");
        } else {
            kit.minLevel = 1;
        }

        if(document.containsKey("menuItem")){
            kit.menuItem = MongoUtils.itemStackFromDocument((Document) document.get("menuItem"));
        } else {
            kit.menuItem = ItemStack.of(ItemTypes.GOLDEN_SWORD, 1);
        }

        ItemUtils.setName(kit.menuItem, kit.name);
        ItemUtils.addLore(kit.menuItem, "&eСтоимость: " + kit.price);
        if(kit.minLevel > 1){
            ItemUtils.addLore(kit.menuItem, "&6Требуемый уровень: " + kit.minLevel);
        }
        ItemUtils.addLore(kit.menuItem, kit.description);

        kit.helmet = MongoUtils.itemStackFromDocument((Document) document.get("helmet"));
        kit.chestplate = MongoUtils.itemStackFromDocument((Document) document.get("chestplate"));
        kit.leggings = MongoUtils.itemStackFromDocument((Document) document.get("leggings"));
        kit.boots = MongoUtils.itemStackFromDocument((Document) document.get("boots"));
        kit.offhand = MongoUtils.itemStackFromDocument((Document) document.get("offhand"));

        List<Document> other = (List<Document>) document.get("other");

        if(other == null){
            return kit;
        }

        for(Document doc : other){
            kit.items.add(MongoUtils.itemStackFromDocument(doc));
        }

        return kit;
    }
}
