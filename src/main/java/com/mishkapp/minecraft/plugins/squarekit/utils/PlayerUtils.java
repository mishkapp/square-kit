package com.mishkapp.minecraft.plugins.squarekit.utils;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

/**
 * Created by mishkapp on 12.12.2016.
 */
public class PlayerUtils {

    public static boolean isInCreativeMode(Player player){
        return player.get(Keys.GAME_MODE).get().equals(GameModes.CREATIVE);
    }

    public static boolean isInSpectatorMode(Player player){
        return player.get(Keys.GAME_MODE).get().equals(GameModes.SPECTATOR);
    }
}
