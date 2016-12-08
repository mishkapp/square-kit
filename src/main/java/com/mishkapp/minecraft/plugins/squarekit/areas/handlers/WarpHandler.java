package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.WarpZonesRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class WarpHandler extends Handler {
    private String warpId;

    public WarpHandler() {
        warpId = "city";
    }

    @Override
    public void tick(Area area) {
        List<Player> players = area.getPlayers();
        if(players.isEmpty()){
            return;
        }

        players.forEach(p -> WarpZonesRegistry.getInstance().warp(p, warpId));
    }

    @Override
    public String serialize() {
        return "warp:" + warpId;
    }

    public static WarpHandler deserialize(String[] args) {
        WarpHandler result = new WarpHandler();
        if (args.length > 0) {
            result.warpId = args[0];
        }
        return result;
    }
}
