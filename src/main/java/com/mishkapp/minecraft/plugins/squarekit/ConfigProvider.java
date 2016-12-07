package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.configs.SpawnConfig;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by mishkapp on 07.12.2016.
 */
public class ConfigProvider {

    private static ConfigProvider instance;

    private SpawnConfig spawnConfig;

    public SpawnConfig getSpawnConfig() {
        return spawnConfig;
    }

    public void saveSpawnConfig() {
        MongoCollection<Document> collection = SquareKit.getInstance().getMongoDb().getCollection("configs");
        collection.findOneAndReplace(eq("id", "spawn"), spawnConfig.toDocument());
    }

    private ConfigProvider(){}

    public void init(MongoDatabase mongoDb){
        MongoCollection<Document> collection = mongoDb.getCollection("configs");

        Document spawnDocument = collection.find(eq("id", "spawn")).first();
        if(spawnDocument != null){
            spawnConfig = SpawnConfig.fromDocument(spawnDocument);
        } else {
            spawnConfig = SpawnConfig.defaultConfig();
        }
    }

    public static ConfigProvider getInstance(){
        if(instance == null){
            instance = new ConfigProvider();
        }
        return instance;
    }
}
