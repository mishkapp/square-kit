package com.mishkapp.minecraft.plugins.squarekit;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 28.06.2016.
 */
public class ItemUtils {

    public static void addLore(ItemStack i, String lore){
        List<Text> list;
        if(i.get(Keys.ITEM_LORE).isPresent()){
            list = i.get(Keys.ITEM_LORE).get();
        } else {
            list = new ArrayList<>();
        }
        for(String s : lore.split("\n")){
            list.add(TextSerializers.FORMATTING_CODE.deserialize(s));
        }
        setLore(i, list);
    }

    public static void setLore(ItemStack i, String lore){
        List<Text> list = new ArrayList<>();
        for(String s : lore.split("\n")){
            list.add(TextSerializers.FORMATTING_CODE.deserialize(s));
        }
        setLore(i, list);
    }

    public static void setLore(ItemStack i, List<Text> lore){
        i.offer(Keys.ITEM_LORE, lore);
    }

    public static boolean isArmor(ItemStack i){
        return (i.getItem() == ItemTypes.LEATHER_BOOTS
                || i.getItem() == ItemTypes.LEATHER_CHESTPLATE
                || i.getItem() == ItemTypes.LEATHER_HELMET
                || i.getItem() == ItemTypes.LEATHER_LEGGINGS

                || i.getItem() == ItemTypes.CHAINMAIL_BOOTS
                || i.getItem() == ItemTypes.CHAINMAIL_CHESTPLATE
                || i.getItem() == ItemTypes.CHAINMAIL_HELMET
                || i.getItem() == ItemTypes.CHAINMAIL_LEGGINGS

                || i.getItem() == ItemTypes.IRON_BOOTS
                || i.getItem() == ItemTypes.IRON_CHESTPLATE
                || i.getItem() == ItemTypes.IRON_HELMET
                || i.getItem() == ItemTypes.IRON_LEGGINGS

                || i.getItem() == ItemTypes.GOLDEN_BOOTS
                || i.getItem() == ItemTypes.GOLDEN_CHESTPLATE
                || i.getItem() == ItemTypes.GOLDEN_HELMET
                || i.getItem() == ItemTypes.GOLDEN_LEGGINGS

                || i.getItem() == ItemTypes.DIAMOND_BOOTS
                || i.getItem() == ItemTypes.DIAMOND_CHESTPLATE
                || i.getItem() == ItemTypes.DIAMOND_HELMET
                || i.getItem() == ItemTypes.DIAMOND_LEGGINGS);
    }

    public static boolean isWeapon(ItemStack i){
        return (i.getItem() == ItemTypes.WOODEN_AXE
                || i.getItem() == ItemTypes.WOODEN_SWORD
                || i.getItem() == ItemTypes.WOODEN_SHOVEL
                || i.getItem() == ItemTypes.WOODEN_HOE
                || i.getItem() == ItemTypes.WOODEN_PICKAXE

                || i.getItem() == ItemTypes.STONE_AXE
                || i.getItem() == ItemTypes.STONE_SWORD
                || i.getItem() == ItemTypes.STONE_SHOVEL
                || i.getItem() == ItemTypes.STONE_HOE
                || i.getItem() == ItemTypes.STONE_PICKAXE

                || i.getItem() == ItemTypes.GOLDEN_AXE
                || i.getItem() == ItemTypes.GOLDEN_SWORD
                || i.getItem() == ItemTypes.GOLDEN_SHOVEL
                || i.getItem() == ItemTypes.GOLDEN_HOE
                || i.getItem() == ItemTypes.GOLDEN_PICKAXE

                || i.getItem() == ItemTypes.IRON_AXE
                || i.getItem() == ItemTypes.IRON_SWORD
                || i.getItem() == ItemTypes.IRON_SHOVEL
                || i.getItem() == ItemTypes.IRON_HOE
                || i.getItem() == ItemTypes.IRON_PICKAXE

                || i.getItem() == ItemTypes.DIAMOND_AXE
                || i.getItem() == ItemTypes.DIAMOND_SWORD
                || i.getItem() == ItemTypes.DIAMOND_SHOVEL
                || i.getItem() == ItemTypes.DIAMOND_HOE
                || i.getItem() == ItemTypes.DIAMOND_PICKAXE);
    }

    public static void setName(ItemStack i, String name){
        i.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(name));
    }

    public static void setColor(ItemStack i, String colorCode){
        String[] split = colorCode.split(":");
        setColor(i,
                Integer.parseInt(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]));
    }

    public static void setColor(ItemStack i, int r, int g, int b){
        if(i.getItem().equals(ItemTypes.LEATHER_BOOTS)
                || i.getItem().equals(ItemTypes.LEATHER_CHESTPLATE)
                || i.getItem().equals(ItemTypes.LEATHER_HELMET)
                || i.getItem().equals(ItemTypes.LEATHER_LEGGINGS)){
            i.offer(Keys.COLOR, Color.ofRgb(r, g, b));
        }
    }
}
