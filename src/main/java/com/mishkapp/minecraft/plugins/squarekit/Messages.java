package com.mishkapp.minecraft.plugins.squarekit;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by mishkapp on 30.06.2016.
 */
public class Messages {


    private static HashMap<String, String> messages = new HashMap<>();

    public static String get(String key){
        if(messages.containsKey(key) && messages.get(key) != null){
            return messages.get(key);
        } else {
            return key;
        }
    }

    public static void init(){
        HashMap<String, String> temp = new HashMap<>();
        MongoCollection collection = SquareKit.getInstance().getMongoDb().getCollection("configs");
        Document document = (Document) collection.find(eq("id", "messages")).first();

        if(document == null){
            return;
        }

        Document msgDoc = (Document)document.get("messages");

        Set<String> keys = msgDoc.keySet();

        keys.forEach(k -> temp.put(k, msgDoc.getString(k)));

        collection = SquareKit.getInstance().getMongoDb().getCollection("messages");
        FindIterable<Document> result = collection.find();
        result.forEach((Consumer<? super Document>) d -> addMessages(temp, d));
        messages = temp;
    }

    private static void addMessages(HashMap<String, String> messages, Document document){
        if(document == null){
            return;
        }

        String id = document.getString("id");
        Document msgDoc = (Document)document.get("messages");

        Set<String> keys = msgDoc.keySet();

        keys.forEach(k -> messages.put(id + "." + k, msgDoc.getString(k)));
    }
}
