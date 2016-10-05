package com.mishkapp.minecraft.plugins.squarekit.serializers;

import com.google.common.reflect.TypeToken;
import com.mishkapp.minecraft.plugins.squarekit.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;

import static com.mishkapp.minecraft.plugins.squarekit.ItemUtils.*;
import static org.spongepowered.api.data.key.Keys.*;

/**
 * Created by mishkapp on 19.09.2016.
 */
public class ItemStackSerializer implements TypeSerializer<ItemStack> {
    @Override
    public ItemStack deserialize(TypeToken<?> type, ConfigurationNode cn) throws ObjectMappingException {
        ConfigurationNode idNode = cn.getNode("id");

        if(idNode.isVirtual()){
            System.out.println("hue");
            return null;
        }

        ItemType itemType = Sponge.getGame().getRegistry().getType(ItemType.class, idNode.getString()).orElse(null);
        if(itemType == null){
            return null;
        }
        ItemStack result = ItemStack.of(itemType, 1);

        Map<String, Object> map = (Map<String, Object>) cn.getValue();
        for (String s : map.keySet()){
            switch (s){
                case "durability": {
                    result.offer(ITEM_DURABILITY, cn.getNode(s).getInt());
                    break;
                }
                case "count": {
                    result.setQuantity(cn.getNode(s).getInt());
                    break;
                }
                case "suffixes": {
                    setLore(result, Utils.CODE_PREFIX + cn.getNode(s).getString());
                    break;
                }
                case "color": {
                    setColor(result, cn.getNode(s).getString());
                    break;
                }
                case "name": {
                    setName(result, cn.getNode(s).getString());
                    break;
                }
                case "unbreakable": {
                    result.offer(UNBREAKABLE, cn.getNode(s).getBoolean());
                    break;
                }
                case "dye-color": {
                    result.offer(DYE_COLOR, Utils.getDyeColor(cn.getNode(s).getString()));
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void serialize(TypeToken<?> type, ItemStack obj, ConfigurationNode value) throws ObjectMappingException {

    }
}
