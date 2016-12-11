package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class AreaRegistry {

    private static AreaRegistry instance;

    private HashMap<String, Area> registry = new HashMap<>();

    private AreaRegistry(){
        Sponge.getScheduler().createTaskBuilder().
                intervalTicks(20).
                execute(() -> registry.values().forEach(Area::tick)).
                submit(SquareKit.getInstance());
    }

    public void add(Area area){
        registry.put(area.getId(), area);
    }

    public Area get(String key){
        if(registry.containsKey(key)){
            return registry.get(key);
        } else {
            return null;
        }
    }

    public boolean isInSafeArea(Player player){
        return getApplicableAreas(player).parallelStream().filter(Area::isSafe).count() > 0;
    }

    public boolean isInSafeArea(Location loc){
        return getApplicableAreas(loc).parallelStream().filter(Area::isSafe).count() > 0;
    }

    public List<Area> getApplicableAreas(Player player){
        return registry.values().parallelStream().filter(a -> a.isInside(player)).collect(Collectors.toList());
    }

    public List<Area> getApplicableAreas(Location loc){
        return registry.values().parallelStream().filter(a -> a.isInside(loc)).collect(Collectors.toList());
    }

    public List<Area> getNearbyAreas(Player player, int distance){
        return registry.values().parallelStream().filter(a ->
            a.getWorld().equals(player.getWorld()) && a.getCenter().distance(player.getLocation().getPosition()) < distance
        ).collect(Collectors.toList());
    }

    public void save(String key){
        if(!(registry.containsKey(key))){
            return;
        }

        registry.get(key).save();
    }

    public void remove(String key){
        if(!(registry.containsKey(key))){
            return;
        }
        registry.get(key).remove();
        registry.remove(key);
    }

    public void load(String key){
        if(registry.containsKey(key)){
            registry.get(key).removeHandlers();
        }
        add(Area.load(key));
    }

    public static AreaRegistry getInstance(){
        if(instance == null){
            instance = new AreaRegistry();
        }
        return instance;
    }
}
