package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by mishkapp on 03.12.2016.
 */
public abstract class Handler {

    public abstract void tick(Area area);

    public void remove(Area area){}

    public abstract String serialize();

    public static Handler deserialize(String code){
        String[] split = code.split(":");
        String[] args = split.length > 1 ? ArrayUtils.subarray(split, 1, split.length) : new String[0];
        switch (split[0]){
            case "money" :
                return MoneyHandler.deserialize(args);
            case "change-kit":
                return ChangeKitHandler.deserialize(args);
            case "warp" :
                return WarpHandler.deserialize(args);
            case "feed" :
                return FeedHandler.deserialize(args);
            case "mana" :
                return ManaHandler.deserialize(args);
            case "health" :
                return HealthHandler.deserialize(args);
            case "visual" :
                return VisualHandler.deserialize(args);
            case "floating-item" :
                return FloatingItemHandler.deserialize(args);
            default:
                return null;
        }
    }
}
