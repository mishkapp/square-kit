package com.mishkapp.minecraft.plugins.squarekit;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.utils.MongoUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class WarpZonesRegistry {

    private static WarpZonesRegistry instance;

    private HashMap<String, List<WarpPoint>> registry = new HashMap<>();

    public void addPoint(String id, World world, Vector3d pos){
        if(registry.containsKey(id)){
            registry.get(id).add(new WarpPoint(world, pos));
        } else {
            registry.put(id, new ArrayList<>());
            registry.get(id).add(new WarpPoint(world, pos));
        }
        save();
    }

    public List<WarpPoint> getPoints(String id) {
        return registry.getOrDefault(id, new ArrayList<>());
    }

    public List<WarpPoint> getNearbyPoints(Player player, int i) {
        List<WarpPoint> result = new ArrayList<>();
        registry.values().parallelStream().forEach(l -> l.parallelStream().forEach(p -> {
            if(p.getWorld().equals(player.getWorld()) && p.getPosition().distance(player.getLocation().getPosition()) < i){
                result.add(p);
            }
        }));
        return result;
    }

    public void remove(String id, int pointId) {
        if(!(registry.containsKey(id))){
            return;
        }
        registry.get(id).remove(pointId);
        save();
    }

    public void save(){
        MongoDatabase mongoDb = SquareKit.getInstance().getMongoDb();
        MongoCollection collection = mongoDb.getCollection("warps");

        for(String id : registry.keySet()){
            List<WarpPoint> points = registry.get(id);
            List<Document> list = new ArrayList<>();
            Document document = new Document();
            document.append("id", id);
            for(WarpPoint p : points){
                list.add(p.toDocument());
            }
            document.append("points", list);

            Document res = (Document) collection.find(eq("id", id)).first();
            if(res != null){
                collection.replaceOne(eq("id", id), document);
            } else {
                collection.insertOne(document);
            }
        }
    }

    public void init(){
        MongoDatabase mongoDb = SquareKit.getInstance().getMongoDb();
        MongoCollection collection = mongoDb.getCollection("warps");
        MongoCursor cursor = collection.find().iterator();

        while (cursor.hasNext()){
            Document doc = (Document) cursor.next();
            String id = doc.getString("id");
            List<WarpPoint> points = new ArrayList<>();

            List<Document> docs = (List<Document>) doc.get("points");

            for(Document d : docs){
                points.add(WarpPoint.fromDocument(d));
            }
            registry.put(id, points);
        }
    }

    public static WarpZonesRegistry getInstance(){
        if(instance == null){
            instance = new WarpZonesRegistry();
        }
        return instance;
    }

    public static class WarpPoint{
        private World world;
        private Vector3d position;

        public WarpPoint(World world, Vector3d position) {
            this.world = world;
            this.position = position;
        }

        public int getNearbyPlayersCount(){
            return world.getEntities(e -> e.getLocation().getPosition().distance(position) < 30).size();
        }


        public World getWorld() {
            return world;
        }

        public Vector3d getPosition() {
            return position;
        }

        public static WarpPoint fromDocument(Document document){
            World world = Sponge.getServer().getWorld(document.getString("world")).orElse(null);
            Vector3d position = MongoUtils.vectorFromDocument((Document) document.get("pos"));

            return new WarpPoint(world, position);
        }

        public Document toDocument(){
            return new Document()
                    .append("world", world.getName())
                    .append("pos", MongoUtils.vectorToDocument(position));
        }
    }
}
