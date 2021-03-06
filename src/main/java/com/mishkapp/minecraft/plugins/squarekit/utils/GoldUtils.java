package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class GoldUtils {

    public static double goldReceived(KitPlayer killed, KitPlayer killer){
        double result = killed.getLevel() * (Math.max(10, killed.getCurrentKillstreak()));
        if(killed.getLevel() != 1){
            result += (killed.getMoney() * (0.02 + (Math.max(0, killed.getCurrentKillstreak() - 10)/200)));
        }
        result += killed.getCurrentKit().getPrice() / 10;
        result *= (Math.max(0, killer.getCurrentKillstreak() - 10) * 0.005) + 1;
        return result;
    }

    public static double goldPenalty(KitPlayer killed){
        double result = 0;
        if(killed.getLevel() != 1){
            result = killed.getMoney() / 20.0;
        }
        return result;
    }
}
