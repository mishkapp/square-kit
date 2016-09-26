package com.mishkapp.minecraft.plugins.squarekit;

import java.util.HashMap;
import java.util.Set;

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

    public Set<String> getKitList(){
        return kits.keySet();
    }


    public static KitRegistry getInstance(){
        if(instance == null){
            instance = new KitRegistry();
        }
        return instance;
    }
}
