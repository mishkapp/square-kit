package com.mishkapp.minecraft.plugins.squarekit.areas;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.areas.handlers.Handler;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by mishkapp on 03.12.2016.
 */
public abstract class Area {

    protected String id;
    protected boolean isSafe = false;
    protected World world;
    protected List<Vector3d> boundPoints;
    protected List<Handler> handlers = new ArrayList<>();

    public void addHandler(Handler h){
        if(h != null){
            handlers.add(h);
        }
    }

    public void tick(){
        handlers.forEach(h -> h.tick(this));
    }

    public List<Player> getPlayers() {
        return Sponge.getServer().getOnlinePlayers().stream()
                .filter(p -> isInside(p)
                        && !(PlayerUtils.isInSpectatorMode(p) || PlayerUtils.isInCreativeMode(p)))
                .collect(Collectors.toList());
    }

    public boolean isInside(KitPlayer p){
        return isInside(p.getMcPlayer());
    }

    public boolean isInside(Player p){
        return isInside(p.getLocation());
    }

    public abstract boolean isInside(Location l);

    public abstract Vector3d getCenter();

    public String getId() {
        return id;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
    }

    public World getWorld() {
        return world;
    }

    public List<Vector3d> getBoundPoints(){
        if(boundPoints == null){
            fillBoundPoints();
        }
        return boundPoints;
    }

    public abstract void fillBoundPoints();

    public void removeHandler(int id){
        if(handlers.size() > id){
            handlers.get(id).remove(this);
            handlers.remove(id);
        }
    }

    public List<Handler> getHandlers() {
        return handlers;
    }

    public void removeHandlers() {
        handlers = new ArrayList<>();
    }

    public static Area fromDocument(Document document){
        Area result;

        Document definition = (Document)document.get("definition");
        switch (definition.getString("type")){
            case "cuboid":
                result = new CuboidArea(
                        document.getString("id"),
                        definition.getString("world"),
                        ((Document)definition.get("min")).getDouble("x"),
                        ((Document)definition.get("min")).getDouble("y"),
                        ((Document)definition.get("min")).getDouble("z"),
                        ((Document)definition.get("max")).getDouble("x"),
                        ((Document)definition.get("max")).getDouble("y"),
                        ((Document)definition.get("max")).getDouble("z")
                        );
                break;
            case "sphere":
                result = new SphereArea(
                        document.getString("id"),
                        definition.getString("world"),
                        ((Document)definition.get("center")).getDouble("x"),
                        ((Document)definition.get("center")).getDouble("y"),
                        ((Document)definition.get("center")).getDouble("z"),
                        ((Document)definition.get("fi")).getDouble("x"),
                        ((Document)definition.get("fi")).getDouble("y"),
                        ((Document)definition.get("fi")).getDouble("z")
                );
                break;
            default:
                System.out.println("BAD AREA DEFINITION: " + document.getString("id"));
                return null;
        }

        result.id = document.getString("id");
        result.isSafe = document.getBoolean("isSafe");
        List<String> handlersCodes = (List<String>) document.get("handlers");
        for(String code : handlersCodes){
            Handler handler = Handler.deserialize(code);
            if(handler != null){
                result.handlers.add(handler);
            }
        }
        return result;
    }

    public Document toDocument(){
        return new Document("id", id)
                .append("isSafe", isSafe)
                .append("definition", definitionToDocument())
                .append("handlers", serializeHandlers());

    }

    protected abstract Document definitionToDocument();

    protected List<String> serializeHandlers(){
        List<String> strings = new ArrayList<>();
        for(Handler h : handlers){
            strings.add(h.serialize());
        }
        return strings;
    }

    public void remove(){
        MongoDatabase mongoDb = SquareKit.getInstance().getMongoDb();
        MongoCollection collection = mongoDb.getCollection("areas");

        Document document = (Document) collection.find(eq("id", id)).first();
        if(document != null){
            collection.deleteOne(eq("id", id));
        }
    }

    public void save(){
        MongoDatabase mongoDb = SquareKit.getInstance().getMongoDb();
        MongoCollection collection = mongoDb.getCollection("areas");

        Document document = (Document) collection.find(eq("id", id)).first();
        if(document != null){
            collection.replaceOne(eq("id", id), toDocument());
        } else {
            collection.insertOne(toDocument());
        }
    }

    public static Area load(String id){
        MongoDatabase mongoDb = SquareKit.getInstance().getMongoDb();
        MongoCollection collection = mongoDb.getCollection("areas");

        Document document = (Document) collection.find(eq("id", id)).first();
        if(document != null){
            return fromDocument(document);
        } else {
            return null;
        }
    }
}
