package com.mishkapp.minecraft.plugins.squarekit.comparators;

import com.mishkapp.minecraft.plugins.squarekit.Kit;

import java.util.Comparator;

/**
 * Created by mishkapp on 12.12.2016.
 */
public class KitComparator implements Comparator<Kit> {
    @Override
    public int compare(Kit o1, Kit o2) {
        if(o1.getMinLevel() == o2.getMinLevel()){
            return Integer.compare(o1.getPrice(), o2.getPrice());
        }

        return Integer.compare(o1.getMinLevel(), o2.getMinLevel());
    }
}
