package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.ItemUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class SuffixFactory {

    public static List<KitItem> getKitItems(KitPlayer kitPlayer){
        List<KitItem> result = new ArrayList<>();
        Player player = kitPlayer.getMcPlayer();
        CarriedInventory inventory = player.getInventory();
        //TODO: this hook smells bad, exclude last slot due to bug
        Iterator<Inventory> slotIterator = inventory.slots().iterator();
        List<Slot> slots = new ArrayList<>();
        while (slotIterator.hasNext()){
            slots.add((Slot)slotIterator.next());
        }
        slots.remove(slots.size() - 1);
        for(Slot slot : slots){
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
        String[] rawCodes = itemCode.split(";");
        KitItem result = new KitItem(kitPlayer, i, itemCode);
        List<Suffix> suffices = new ArrayList<>();

        for(String c : rawCodes){
            int id = Utils.getBase64Value(c.substring(0, 2));
            String[] args = c.split(":");
            args = Arrays.copyOfRange(args, 1, args.length);
            Suffix suffix = Utils.instantiateSuffix(SuffixRegistry.getInstance().getSuffix(id), kitPlayer, i, args);
            if(suffix == null){
                continue;
            }
            ItemUtils.addLore(i, suffix.getLoreEntry());
            suffices.add(suffix);
        }
        result.setSuffices(suffices);
        return result;
    }
}
