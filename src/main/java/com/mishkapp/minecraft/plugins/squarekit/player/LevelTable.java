package com.mishkapp.minecraft.plugins.squarekit.player;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class LevelTable {

    public static final int MAX_LEVEL = 50;

    public static final int[] experiences = new int[MAX_LEVEL];

    static {
        //TODO: formula needs to be rethought
        for(int i = 0; i < MAX_LEVEL; i++){
            experiences[i] = (i * i) + 100;
        }
    }
}
