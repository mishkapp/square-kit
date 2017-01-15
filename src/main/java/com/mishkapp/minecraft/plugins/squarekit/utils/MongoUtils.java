package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.flowpowered.math.vector.Vector3d;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.ItemUtils.*;
import static org.spongepowered.api.data.key.Keys.*;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class MongoUtils {

    public static Document vectorToDocument(Vector3d vector){
        return new Document()
                .append("x", vector.getX())
                .append("y", vector.getY())
                .append("z", vector.getZ());
    }

    public static Vector3d vectorFromDocument(Document document){
        return new Vector3d(
                document.getDouble("x"),
                document.getDouble("y"),
                document.getDouble("z")
                );
    }

    public static ItemStack itemStackFromDocument(Document document){
        if(document == null){
            return null;
        }
        ItemType itemType = Sponge.getGame().getRegistry().getType(ItemType.class, document.getString("id")).orElse(null);
        if(itemType == null){
            return null;
        }
        ItemStack result = ItemStack.of(itemType, 1);

        for (String s : document.keySet()){
            switch (s){
                case "durability": {
                    result.offer(ITEM_DURABILITY, document.getInteger(s));
                    break;
                }
                case "count": {
                    result.setQuantity(document.getInteger(s));
                    break;
                }
                case "suffixes": {
                    setLore(result, Utils.CODE_PREFIX + "&k" + document.getString(s));
                    break;
                }
                case "color": {
                    setColor(result, document.getString(s));
                    break;
                }
                case "name": {
                    setName(result, document.getString(s));
                    break;
                }
                case "unbreakable": {
                    result.offer(UNBREAKABLE, document.getBoolean(s));
                    break;
                }
                case "dye-color": {
                    result.offer(DYE_COLOR, Utils.getDyeColor(document.getString(s)));
                    break;
                }
            }
        }
        return result;
    }
}
