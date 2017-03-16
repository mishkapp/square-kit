package com.mishkapp.minecraft.plugins.squarekit;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by mishkapp on 28.06.2016.
 */
public class KitRegistry {

    private static KitRegistry instance;

    private HashMap<String, Kit> kits = new HashMap<>();

    public void registerKit(String kitCode, Kit kit){
        kits.put(kitCode, kit);
    }

    public void purge(){
        kits = new HashMap<>();
    }

    public Kit getKit(String kitCode){
        return kits.get(kitCode);
    }

    public Collection<Kit> getKitList(){
        return kits.values();
    }

    public static KitRegistry getInstance(){
        if(instance == null){
            instance = new KitRegistry();
        }
        return instance;
    }

    public void init() {
        MongoCollection collection = SquareKit.getInstance().getMongoDb().getCollection("kits");
        MongoCursor cursor = collection.find().iterator();

        while (cursor.hasNext()){
            Document kitDoc = (Document) cursor.next();
            registerKit(kitDoc.getString("id"), Kit.fromDocument(kitDoc));
        }
    }

    public Kit getDefaultKit() {
        //TODO: need to be generalized
        return getKit("recruit");
    }
}
