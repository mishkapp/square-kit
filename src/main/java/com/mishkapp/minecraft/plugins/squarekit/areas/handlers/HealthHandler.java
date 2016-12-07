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

import static org.spongepowered.api.text.format.TextColors.LIGHT_PURPLE;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class HealthHandler extends Handler {
    private ServerBossBar bossBar = ServerBossBar.builder()
            .color(BossBarColors.PINK)
            .overlay(BossBarOverlays.PROGRESS)
            .name(Text.of("HEALTH"))
            .percent(1.0f)
            .build();

    private double baseHp;

    public HealthHandler() {
        baseHp = 1.0;
    }

    @Override
    public void tick(Area area) {
        bossBar.removePlayers(bossBar.getPlayers());
        List<Player> players = area.getPlayers();
        if(players.isEmpty() || Sponge.getServer().getOnlinePlayers().size() < 1){
            return;
        }
        bossBar.addPlayers(players);
        double hpAdd = ((baseHp * (1 + (0.025 * (players.size() - 1))))/(players.size()));
        bossBar.setName(Text.builder().color(LIGHT_PURPLE).append(Text.of("Здоровье: " + FormatUtils.thousandth(hpAdd) + "/сек")).build());
        players.forEach(p -> PlayersRegistry.getInstance().getPlayer(p.getUniqueId()).addMana(hpAdd));
    }

    @Override
    public String serialize() {
        return "health:" + FormatUtils.unsignedTenth(baseHp);
    }

    public static HealthHandler deserialize(String[] args){
        HealthHandler result = new HealthHandler();
        if(args.length > 0){
            result.baseHp = Double.parseDouble(args[0]);
        }
        return result;
    }
}
