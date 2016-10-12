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

    public static List<KitItem> getKitItems(KitPlayer kitPlayer){
        List<KitItem> result = new ArrayList<>();
        Player player = kitPlayer.getMcPlayer();
        CarriedInventory inventory = player.getInventory();
        for(Object o : inventory.slots()){
            Slot slot = (Slot)o;
            ItemStack i = slot.peek().orElse(null);
            if(i != null && Utils.isKitItem(i)){
                result.add(getKitItem(kitPlayer, i));
                slot.set(i);
            }
        }
        return result;
    }

    public static KitItem getKitItem(KitPlayer kitPlayer, ItemStack i){
        String itemCode = Utils.getItemCode(i);
        ItemUtils.setLore(i, Utils.CODE_PREFIX + itemCode);
        KitItem result = new KitItem(kitPlayer, i, itemCode);
        List<Suffix> suffices = new ArrayList<>();

        while(true){
            if(itemCode.length() < 3){
                break;
            }
            int id = Utils.getBase64Value(itemCode.substring(0, 2));
            itemCode = itemCode.substring(2);
            int level = Utils.getBase64Value(itemCode.substring(0, 1));
            itemCode = itemCode.substring(1);

            Suffix suffix = Utils.instantiateSuffix(SuffixRegistry.getInstance().getSuffix(id), kitPlayer, i, level);
            if(suffix == null){
                continue;
            }
            suffices.add(suffix);
            ItemUtils.addLore(i, suffix.getLoreEntry());
        }
        result.setSuffices(suffices);
        return result;
    }
}
