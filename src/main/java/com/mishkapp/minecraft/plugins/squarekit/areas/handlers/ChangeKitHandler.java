package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.areas.Area;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class ChangeKitHandler extends Handler {
    @Override
    public void tick(Area area) {

    }

    @Override
    public String serialize() {
        return "change-kit";
    }

    public static ChangeKitHandler deserialize(String[] args){
        return new ChangeKitHandler();
    }
}
