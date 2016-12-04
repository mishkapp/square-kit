package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class MoneyHandler extends Handler {
    private double moneyPerTick;

    public MoneyHandler() {
        moneyPerTick = 10.0;
    }

    @Override
    public void tick(Area area) {
        List<Player> players = area.getPlayers();
        if(players.isEmpty()){
            return;
        }
        int moneyAdd = (int) (moneyPerTick/(players.size()) + (moneyPerTick * 0.1 * players.size()));
        players.forEach(p -> PlayersRegistry.getInstance().getPlayer(p.getUniqueId()).addMoney(moneyAdd));
    }

    @Override
    public String serialize() {
        return "money:" + FormatUtils.unsignedTenth(moneyPerTick);
    }

    public static MoneyHandler deserialize(String[] args){
        MoneyHandler result = new MoneyHandler();
        if(args.length > 0){
            result.moneyPerTick = Double.parseDouble(args[0]);
        }
        return result;
    }
}
