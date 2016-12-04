package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.mishkapp.minecraft.plugins.squarekit.KitRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class GoldUtils {

    public static int goldReceived(KitPlayer killed, KitPlayer killer){
        int result = killed.getLevel() * (10 + Math.min(0, killed.getCurrentKillstreak() - 10));
        if(killed.getLevel() != 1){
            result += (killed.getMoney() * (0.02 + (Math.min(0, killed.getCurrentKillstreak() - 10)/2.0)));
        }
        result += KitRegistry.getInstance().getKit(killed.getCurrentKit()).getPrice() / 10;
        result *= Math.min(0, killer.getCurrentKillstreak() - 10) * 0.005;
        return result;
    }

    public static int goldPenalty(KitPlayer killed){
        int result = 0;
        if(killed.getLevel() != 1){
            result = killed.getMoney() / 20;
        }
        return result;
    }
}