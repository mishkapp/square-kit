package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Optional;

import static org.spongepowered.api.data.type.DyeColors.*;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class Utils {
    public static final String CODE_PREFIX = "#";

    private static final char[] base64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static boolean isKitItem(ItemStack i){
        if(i == null){
            return false;
        }
        Optional<List<Text>> optionalLore = i.get(Keys.ITEM_LORE);
        boolean result = optionalLore.isPresent();
        return result && optionalLore.get().get(0).toPlain().startsWith(CODE_PREFIX);
    }

    public static boolean isKitItem(ItemStackSnapshot i){
        if(i == null){
            return false;
        }
        Optional<List<Text>> optionalLore = i.get(Keys.ITEM_LORE);
        boolean result = optionalLore.isPresent();
        return result && optionalLore.get().get(0).toPlain().startsWith(CODE_PREFIX);
    }

    public static String getItemCode(ItemStack i){
        Optional<List<Text>> optionalLore = i.get(Keys.ITEM_LORE);
        if(!optionalLore.isPresent()){
            return "";
        }
        return optionalLore.get().get(0).toPlain().substring(CODE_PREFIX.length());
    }

    public static int getBase64Value(String s){
        int result = 0;
        int i = s.length() - 1;
        for(char c : s.toCharArray()){
            result += getBase64Index(c) * Math.pow(64, i);
            i--;
        }
        return result;
    }

    public static int getBase64Index(char c){
        for(int i = 0; i < base64.length; i++){
            if(base64[i] == c){
                return i;
            }
        }
        return -1;
    }

    public static Suffix instantiateSuffix(Class<? extends Suffix> clazz, KitPlayer kitPlayer, ItemStack i, int level){
        try{
            return clazz.getConstructor(KitPlayer.class, ItemStack.class, Integer.class).newInstance(
                    kitPlayer,
                    i,
                    level
            );
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static DyeColor getDyeColor(String s) {
        switch (s){
            case "BLACK": {
                return BLACK;
            }
            case "BLUE": {
                return BLUE;
            }
            case "BROWN": {
                return BROWN;
            }
            case "CYAN": {
                return CYAN;
            }
            case "GRAY": {
                return GRAY;
            }
            case "GREEN": {
                return GREEN;
            }
            case "LIGHT_BLUE": {
                return LIGHT_BLUE;
            }
            case "LIME": {
                return LIME;
            }
            case "MAGENTA": {
                return MAGENTA;
            }
            case "ORANGE": {
                return ORANGE;
            }
            case "PINK": {
                return PINK;
            }
            case "PURPLE": {
                return PURPLE;
            }
            case "RED": {
                return RED;
            }
            case "SILVER": {
                return SILVER;
            }
            case "WHITE": {
                return WHITE;
            }
            case "YELLOW": {
                return YELLOW;
            }
            default: {
                return WHITE;
            }
        }
    }

    public static Text _text(String s){
        return TextSerializers.FORMATTING_CODE.deserialize(s);
    }
}
