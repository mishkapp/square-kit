package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import org.spongepowered.api.Sponge;

import java.util.HashMap;

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
        add(Area.load(key));
    }

    public static AreaRegistry getInstance(){
        if(instance == null){
            instance = new AreaRegistry();
        }
        return instance;
    }
}
