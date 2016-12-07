package com.mishkapp.minecraft.plugins.squarekit;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;

/**
 * Created by mishkapp on 30.06.2016.
 */
public class Messages {


    private static HashMap<String, String> messages;

    public static String get(String key){
        if(messages.containsKey(key)){
            return messages.get(key);
        } else {
            return key;
        }
    }

    public static void init(ConfigurationNode config){
        messages = (HashMap<String, String>)config.getValue();
    }
}
