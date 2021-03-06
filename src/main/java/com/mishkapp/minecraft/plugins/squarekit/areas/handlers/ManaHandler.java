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

import static org.spongepowered.api.text.format.TextColors.BLUE;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class ManaHandler extends Handler{
    private ServerBossBar bossBar = ServerBossBar.builder()
            .color(BossBarColors.BLUE)
            .overlay(BossBarOverlays.PROGRESS)
            .name(Text.of("MANA"))
            .percent(1.0f)
            .build();

    private double baseMana;

    public ManaHandler() {
        baseMana = 1.0;
    }

    @Override
    public void tick(Area area) {
        bossBar.removePlayers(bossBar.getPlayers());
        List<Player> players = area.getPlayers();
        if(players.isEmpty() || Sponge.getServer().getOnlinePlayers().size() < 1){
            return;
        }
        bossBar.addPlayers(players);
        double manaAdd = ((baseMana * (1 + (0.025 * (players.size() - 1))))/(players.size()));
        bossBar.setName(Text.builder().color(BLUE).append(Text.of("Мана: " + FormatUtils.hundredth(manaAdd) + "/сек")).build());
        players.forEach(p -> PlayersRegistry.getInstance().getPlayer(p).addMana(manaAdd));
    }

    @Override
    public void remove(Area area){
        bossBar.removePlayers(bossBar.getPlayers());
    }

    @Override
    public String serialize() {
        return "mana:" + FormatUtils.unsignedTenth(baseMana);
    }

    public static ManaHandler deserialize(String[] args){
        ManaHandler result = new ManaHandler();
        if(args.length > 0){
            result.baseMana = Double.parseDouble(args[0]);
        }
        return result;
    }
}
