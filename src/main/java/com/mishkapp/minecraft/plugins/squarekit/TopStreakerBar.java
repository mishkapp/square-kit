package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by mishkapp on 05.12.2016.
 */
public class TopStreakerBar {

    private static TopStreakerBar instance;

    private ServerBossBar bossBar = ServerBossBar.builder()
            .playEndBossMusic(false)
            .color(BossBarColors.RED)
            .overlay(BossBarOverlays.NOTCHED_20)
            .name(Text.of("STREAK"))
            .visible(true)
            .build();

    private TopStreakerBar(){

    }

    public void update(){
        List<KitPlayer> players = PlayersRegistry.getInstance().getPlayers();
        KitPlayer kitPlayer = players.stream()
                .sorted((p1, p2) -> -1 * Integer.compare(p1.getCurrentKillstreak(), p2.getCurrentKillstreak()))
                .findFirst().orElse(null);
        if(kitPlayer == null){
            return;
        }

        bossBar.setName(Text.builder()
                .color(TextColors.RED)
                .append(kitPlayer.getMcPlayer().getDisplayNameData().displayName().get())
                .append(Text.of(" [" + kitPlayer.getCurrentKillstreak() + "]")).build());
        bossBar.setPercent((float) Math.min(1, kitPlayer.getHealth()/kitPlayer.getMaxHealth()));
        render();
    }

    public void render(){
        bossBar.addPlayers(Sponge.getServer().getOnlinePlayers());
    }

    public void init(){
        Sponge.getScheduler().createTaskBuilder()
                .intervalTicks(30)
                .execute(r -> {
                    this.update();
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    public static TopStreakerBar getInstance(){
        if(instance == null){
            instance = new TopStreakerBar();
        }
        return instance;
    }

}
