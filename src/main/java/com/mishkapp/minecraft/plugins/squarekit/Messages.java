package com.mishkapp.minecraft.plugins.squarekit;

import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;

/**
 * Created by mishkapp on 30.06.2016.
 */
public class Messages {


    private static HashMap<String, String> messages;

    public static String getMessage(String key){
        if(messages.containsKey(key)){
            return messages.get(key);
        } else {
            return key;
        }
    }

    public static void addMessage(String key, String value){
        messages.put(key, value);
    }

    public static void init(ConfigurationNode config){
//        messages = (HashMap<String, String>)config.getValue();
        System.out.println("messages = " + config.getValue());
    }
}
