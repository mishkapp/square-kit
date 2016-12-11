package com.mishkapp.minecraft.plugins.squarekit;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;
import static java.lang.Math.max;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class BountyHandler {

    private static BountyHandler instance;

    private ServerBossBar bossBar = ServerBossBar.builder()
            .playEndBossMusic(false)
            .color(BossBarColors.RED)
            .overlay(BossBarOverlays.NOTCHED_20)
            .name(Text.of("BOUNTY"))
            .visible(true)
            .build();

    private ParticleEffect trail = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.REDSTONE_DUST)
            .build();

    private HashMap<Long, Vector3d> trailPoints = new HashMap<>();

    private KitPlayer lastPlayer;

    private int renderTick = 30;
    private int currentTick = 30;

    private BountyHandler(){}

    public void update(){
        drawTrail();
        if(currentTick < renderTick){
            currentTick += 1;
            return;
        }
        currentTick = 0;
        List<KitPlayer> players = PlayersRegistry.getInstance().getPlayers();
        KitPlayer kitPlayer = players.stream()
                .sorted((p1, p2) -> -1 * Integer.compare(p1.getBounty(), p2.getBounty()))
                .findFirst().orElse(null);
        if(kitPlayer == null){
            kitPlayer = players.stream()
                    .sorted((p1, p2) -> -1 * Integer.compare(p1.getCurrentKillstreak(), p2.getCurrentKillstreak()))
                    .findFirst().orElse(null);
            if(kitPlayer == null){
                return;
            }
        }

        int bounty = kitPlayer.getBounty();
        lastPlayer = kitPlayer;

        Text.Builder builder = Text.builder();

        builder.color(TextColors.RED);
        builder.append(kitPlayer.getMcPlayer().getDisplayNameData().displayName().get());
        int treshold = KitRegistry.getInstance().getKit(kitPlayer.getCurrentKit()).getPrice() * 10;
        treshold = max(1000, treshold);
        if(kitPlayer.getBounty() >= treshold){
            builder.append(_text(" (" + KitRegistry.getInstance().getKit(kitPlayer.getCurrentKit()).getName() + "&c) "));
        }
        builder.append(Text.of(" - убийств: " + kitPlayer.getCurrentKillstreak()));
        if(bounty > 0){
            builder.append(Text.of(" - награда за голову: "));
            builder.append(_text("&6" + kitPlayer.getBounty()));
        }

        bossBar.setName(builder.build());
        bossBar.setPercent((float) Math.min(1, kitPlayer.getHealth()/kitPlayer.getMaxHealth()));
        render();
    }

    private void render(){
        bossBar.addPlayers(Sponge.getServer().getOnlinePlayers());
    }

    private void drawTrail(){

        trailPoints.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getKey() > 10 * 1000);

        if(lastPlayer == null){
            return;
        }
        if(!lastPlayer.getMcPlayer().isOnline()) {
            return;
        }
        int treshold = KitRegistry.getInstance().getKit(lastPlayer.getCurrentKit()).getPrice() * 10;
        treshold = max(1000, treshold);
        if(lastPlayer.getBounty() < treshold){
            return;
        }
        Player player = lastPlayer.getMcPlayer();
        trailPoints.put(System.currentTimeMillis(), player.getLocation().getPosition().add(0, 0.5, 0));

        trailPoints.values().forEach(
                v -> {
                    player.getWorld().spawnParticles(
                            trail,
                            v
                    );
                }
        );
    }

    public void init(){
        Sponge.getScheduler().createTaskBuilder()
                .intervalTicks(2)
                .execute(r -> {
                    this.update();
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    public static BountyHandler getInstance(){
        if(instance == null){
            instance = new BountyHandler();
        }
        return instance;
    }

    public List<KitPlayer> getList() {
        List<KitPlayer> result = PlayersRegistry.getInstance().getPlayers().stream()
                .sorted((p1, p2) -> -1 * Integer.compare(p1.getBounty(), p2.getBounty())).collect(Collectors.toList());
        if(result.size() < 10){
            List<KitPlayer> streak = PlayersRegistry.getInstance().getPlayers().stream()
                    .sorted((p1, p2) -> -1 * Integer.compare(p1.getCurrentKillstreak(), p2.getCurrentKillstreak())).collect(Collectors.toList());
            streak.removeIf(result::contains);
            int size = result.size();
            for(int i = 0; i < 10 - size; i++){
                if((i + 1) > streak.size()){
                    return result;
                }
                result.add(streak.get(i));
            }
        }
        return result;
    }

    public void killed(KitPlayer killed, KitPlayer killer) {
        if(killed.getBounty() <= 0){
            return;
        }

        killer.addMoney(killed.getBounty(), false);
        if(killed.getBounty() >= 1000) {
            Sponge.getServer().getOnlinePlayers().forEach(
                    p -> {
                        p.sendMessage(_text(
                                Messages.get("bounty-complete")
                                        .replace("%PLAYER%", killed.getMcPlayer().getName())
                                        .replace("%KILLER%", killer.getMcPlayer().getName())
                                        .replace("%BOUNTY%", killed.getBounty() + "")
                        ));
                    }
            );
        }
        killed.setBounty(0);
    }

    public void denied(KitPlayer killed) {
        if(killed.getBounty() <= 0){
            return;
        }

        killed.subtractMoney(killed.getBounty()/2, false);

        if(killed.getBounty() >= 1000){
            Sponge.getServer().getOnlinePlayers().forEach(
                    p -> {
                        p.sendMessage(_text(
                                Messages.get("bounty-denied")
                                        .replace("%PLAYER%", killed.getMcPlayer().getName())
                                        .replace("%BOUNTY%", killed.getBounty()/2 + "")
                        ));
                    }
            );
        }

        killed.setBounty(0);
    }
}
