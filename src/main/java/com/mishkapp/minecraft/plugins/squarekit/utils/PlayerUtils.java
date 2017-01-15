package com.mishkapp.minecraft.plugins.squarekit.utils;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.ArrayList;
import java.util.List;

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

    public static void applyEffects(Entity entity, PotionEffect... effects){
        List<PotionEffect> entityEffects = entity.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
        for(PotionEffect pe : effects){
            entityEffects.add(pe);
        }
        entity.offer(Keys.POTION_EFFECTS, entityEffects);
    }
}
