package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;
import static java.lang.Math.abs;

/**
 * Created by mishkapp on 17.01.2017.
 */
public class ManaPercentHandler extends Handler {
    private ServerBossBar bossBar = ServerBossBar.builder()
            .color(BossBarColors.BLUE)
            .overlay(BossBarOverlays.PROGRESS)
            .name(Text.of("HEALTH"))
            .percent(1.0f)
            .build();

    private double regenMultiplier = 1.0;

    public ManaPercentHandler() {}

    @Override
    public void tick(Area area) {
        bossBar.removePlayers(bossBar.getPlayers());
        List<Player> players = area.getPlayers();
        if(players.isEmpty()){
            return;
        }
        bossBar.addPlayers(players);
        bossBar.setName(_text(Messages.get("areas.mana-percent-bossbar-title").replace("%REGEN%", FormatUtils.unsignedRound(regenMultiplier * 100))));
        players.forEach(p -> {
            KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(p);
            kitPlayer.addMana(abs(kitPlayer.getManaRegen()) * 4 * regenMultiplier);
        });
    }

    @Override
    public void remove(Area area){
        bossBar.removePlayers(bossBar.getPlayers());
    }

    @Override
    public String serialize() {
        return "mana-percent:" + FormatUtils.unsignedTenth(regenMultiplier);
    }

    public static ManaPercentHandler deserialize(String[] args){
        ManaPercentHandler result = new ManaPercentHandler();
        if(args.length > 0){
            result.regenMultiplier = Double.parseDouble(args[0]);
        }
        return result;
    }
}
