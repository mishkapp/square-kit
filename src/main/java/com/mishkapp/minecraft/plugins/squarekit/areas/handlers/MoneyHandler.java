package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

import static org.spongepowered.api.text.format.TextColors.GOLD;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class MoneyHandler extends Handler {
    private ServerBossBar bossBar = ServerBossBar.builder()
            .color(BossBarColors.YELLOW)
            .overlay(BossBarOverlays.PROGRESS)
            .name(Text.of("MONEY"))
            .percent(1.0f)
            .build();

    private double moneyPerTick;

    public MoneyHandler() {
        moneyPerTick = 10.0;
    }

    @Override
    public void tick(Area area) {
        bossBar.removePlayers(bossBar.getPlayers());
        List<Player> players = area.getPlayers();
        if(players.isEmpty() || Sponge.getServer().getOnlinePlayers().size() < 10){
            return;
        }
        bossBar.addPlayers(players);
        double moneyAdd = ((moneyPerTick * (1 + (0.025 * players.size() - 1)))/(players.size()));
        bossBar.setName(Text.builder().color(GOLD).append(Text.of("Деньги: " + moneyAdd + "/сек")).build());
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
