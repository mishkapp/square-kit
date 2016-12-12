package com.mishkapp.minecraft.plugins.squarekit.comparators;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;

import java.util.Comparator;

/**
 * Created by mishkapp on 12.12.2016.
 */
public class BountyComparator implements Comparator<KitPlayer> {
    @Override
    public int compare(KitPlayer o1, KitPlayer o2) {
        if(o1.getBounty() == o2.getBounty()){
            return -1 * Integer.compare(o1.getCurrentKillstreak(), o2.getCurrentKillstreak());
        }

        return -1 * Integer.compare(o1.getBounty(), o2.getBounty());
    }
}
