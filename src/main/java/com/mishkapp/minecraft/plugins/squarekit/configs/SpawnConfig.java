package com.mishkapp.minecraft.plugins.squarekit.configs;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.utils.MongoUtils;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

/**
 * Created by mishkapp on 07.12.2016.
 */
public class SpawnConfig {
    private World world;
    private Vector3d location;
    private Vector3d rotation;

    public SpawnConfig(World world, Vector3d location, Vector3d rotation) {
        this.world = world;
        this.location = location;
        this.rotation = rotation;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Vector3d getLocation() {
        return location;
    }

    public void setLocation(Vector3d location) {
        this.location = location;
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.rotation = rotation;
    }

    public static SpawnConfig defaultConfig(){
        World world = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).orElse(null);
        return new SpawnConfig(
                world,
                world.getSpawnLocation().getPosition(),
                new Vector3d(0, 0, 0)
        );
    }

    public static SpawnConfig fromDocument(Document document){
        if(document.containsKey("world") && document.containsKey("location") && document.containsKey("rotation")){
            return new SpawnConfig(
                    Sponge.getServer().getWorld(document.getString("world")).get(),
                    MongoUtils.vectorFromDocument((Document) document.get("location")),
                    MongoUtils.vectorFromDocument((Document) document.get("rotation"))
            );
        } else {
            return defaultConfig();
        }
    }

    public Document toDocument(){
        return new Document("id", "world")
                .append("world", world.getName())
                .append("location", MongoUtils.vectorToDocument(location))
                .append("rotation", MongoUtils.vectorToDocument(rotation));
    }
}
