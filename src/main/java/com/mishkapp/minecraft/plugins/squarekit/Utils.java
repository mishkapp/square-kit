package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

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

    public static Suffix instantiateSuffix(Class<? extends Suffix> clazz, ItemStack i, int level){
        try{
            return clazz.getConstructor(ItemStack.class, Integer.class).newInstance(i, level);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static PotionEffect getEffectByLevel(Integer level, int time) {

        int effectId = level/10;
        int effectLevel = level % 10;
        PotionEffectType effectType;
        //TODO: Effects names differs from bukkit effects, need to refactor this
        switch (effectId){
            case 0: {
                effectType = PotionEffectTypes.SPEED;
                break;
            }
            case 1: {
                effectType = PotionEffectTypes.SLOWNESS;
                break;
            }
            case 2: {
                effectType = PotionEffectTypes.HASTE;
                break;
            }
            case 3: {
                effectType = PotionEffectTypes.MINING_FATIGUE;
                break;
            }
            case 4: {
                effectType = PotionEffectTypes.STRENGTH;
                break;
            }
            case 5: {
                effectType = PotionEffectTypes.REGENERATION;
                break;
            }
//            case 6: {
//                effectType = PotionEffectTypes.HARM;
//                break;
//            }
            case 7: {
                effectType = PotionEffectTypes.JUMP_BOOST;
                break;
            }
//            case 8: {
//                effectType = PotionEffectTypes.CONFUSION;
//                break;
//            }
            case 9: {
                effectType = PotionEffectTypes.REGENERATION;
                break;
            }
            case 10: {
                effectType = PotionEffectTypes.RESISTANCE;
                break;
            }
            case 11: {
                effectType = PotionEffectTypes.FIRE_RESISTANCE;
                break;
            }
            case 12: {
                effectType = PotionEffectTypes.WATER_BREATHING;
                break;
            }
            case 13: {
                effectType = PotionEffectTypes.INVISIBILITY;
                break;
            }
            case 14: {
                effectType = PotionEffectTypes.BLINDNESS;
                break;
            }
            case 15: {
                effectType = PotionEffectTypes.NIGHT_VISION;
                break;
            }
            case 16: {
                effectType = PotionEffectTypes.HUNGER;
                break;
            }
            case 17: {
                effectType = PotionEffectTypes.WEAKNESS;
                break;
            }
            case 18: {
                effectType = PotionEffectTypes.POISON;
                break;
            }
            case 19: {
                effectType = PotionEffectTypes.WITHER;
                break;
            }
            case 20: {
                effectType = PotionEffectTypes.HEALTH_BOOST;
                break;
            }
            case 21: {
                effectType = PotionEffectTypes.ABSORPTION;
                break;
            }
            case 22: {
                effectType = PotionEffectTypes.SATURATION;
                break;
            }
//            case 23: {
//                effectType = PotionEffectTypes.GLOWING;
//                break;
//            }
//            case 24: {
//                effectType = PotionEffectTypes.LEVITATION;
//                break;
//            }
//            case 25: {
//                effectType = PotionEffectTypes.LUCK;
//                break;
//            }
//            case 26: {
//                effectType = PotionEffectTypes.UNLUCK;
//                break;
//            }
            default: {
                effectType = PotionEffectTypes.SPEED;
            }
        }

        return PotionEffect.builder()
                .potionType(effectType)
                .amplifier(effectLevel)
                .duration(time)
                .build();
    }
}
