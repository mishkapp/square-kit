package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;

import java.util.HashMap;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class SuffixRegistry {

    private HashMap<Integer, Class<? extends Suffix>> suffixes = new HashMap<>();

    private static SuffixRegistry instance;

    private SuffixRegistry(){}

    public void registerSuffix(int id, Class<? extends Suffix> s){
        if(suffixes.containsKey(id)){
            throw new RuntimeException("Ability with given id (" + id + ") already presents in registry!");
        } else {
            suffixes.put(id, s);
        }
    }

    public Class<? extends Suffix> getSuffix(int id){
        return suffixes.get(id);
    }

    public void purge(){
        suffixes = new HashMap<>();
    }

    public static SuffixRegistry getInstance() {
        if(instance == null){
            instance = new SuffixRegistry();
        }
        return instance;
    }
}
