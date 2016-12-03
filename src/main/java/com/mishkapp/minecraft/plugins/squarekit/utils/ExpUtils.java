package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class ExpUtils {

    public static int expReceived(KitPlayer killed, KitPlayer killer){
        int result = killed.getLevel();
        result *= 10 + Math.min(0, killed.getCurrentKillstreak() - 10);
        result *= Math.min(0, killer.getCurrentKillstreak() - 10) * 0.01;
        return result;
    }

    public static int expPenalty(KitPlayer killed){
        int result = 0;
        if(killed.getCurrentKillstreak() < 1){
            result = killed.getLevel() * 10;
        }
        return result;
    }
}
