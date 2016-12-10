package com.mishkapp.minecraft.plugins.squarekit;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.HashMap;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by mishkapp on 30.06.2016.
 */
public class Messages {


    private static HashMap<String, String> messages;

    public static String get(String key){
        return messages.getOrDefault(key, key);
    }

    public static void init(){
        messages = new HashMap<>();
        MongoCollection collection = SquareKit.getInstance().getMongoDb().getCollection("configs");
        Document document = (Document) collection.find(eq("id", "messages")).first();

        if(document == null){
            return;
        }

        Set<String> keys = ((Document)document.get("messages")).keySet();

        keys.forEach(k -> messages.put(k, document.getString(k)));
    }
}
