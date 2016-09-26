package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class SuffixFactory {

    public static List<Suffix> getSuffixes(Player player){
        List<Suffix> result = new ArrayList<>();
        CarriedInventory inventory = player.getInventory();
        for(Object o : inventory.slots()){
            Slot slot = (Slot)o;
            ItemStack i = slot.peek().orElse(null);
            if(i != null && Utils.isKitItem(i)){
                result.addAll(getSuffixes(i));
                slot.set(i);
            }
        }
        return result;
    }

    public static List<Suffix> getSuffixes(ItemStack i){
        String itemCode = Utils.getItemCode(i);
        ItemUtils.setLore(i, Utils.CODE_PREFIX + itemCode);
        List<Suffix> result = new ArrayList<>();

        while(true){
            if(itemCode.length() < 4){
                break;
            }
            int id = Utils.getBase64Value(itemCode.substring(0, 2));
            itemCode = itemCode.substring(2);
            int level = Utils.getBase64Value(itemCode.substring(0, 2));
            itemCode = itemCode.substring(2);

            Suffix suffix = Utils.instantiateSuffix(SuffixRegistry.getInstance().getSuffix(id), i, level);
            if(suffix == null){
                continue;
            }
            result.add(suffix);
            ItemUtils.addLore(i, suffix.getLoreEntry());
        }
        return result;
    }
}
