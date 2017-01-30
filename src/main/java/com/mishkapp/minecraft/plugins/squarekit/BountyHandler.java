package com.mishkapp.minecraft.plugins.squarekit;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.comparators.BountyComparator;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

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
        List<KitPlayer> players = getList();
        if(players.size() == 0){
            return;
        }
        KitPlayer kitPlayer = players.get(0);
        int bounty = kitPlayer.getBounty();
        lastPlayer = kitPlayer;

        String bossbarTitle = Messages.get("bounty.bossbar-name").replace("%PLAYER%", kitPlayer.getMcPlayer().getDisplayNameData().displayName().get().toPlain());
        int treshold = KitRegistry.getInstance().getKit(kitPlayer.getCurrentKit()).getPrice() * 10;
        treshold = max(1000, treshold);
        if(kitPlayer.getBounty() >= treshold){
            bossbarTitle += Messages.get("bounty.bossbar-kit").replace("%KIT%", KitRegistry.getInstance().getKit(kitPlayer.getCurrentKit()).getName());
        }
        bossbarTitle += Messages.get("bounty.bossbar-kills").replace("%KILLS%", Integer.toString(kitPlayer.getCurrentKillstreak()));
        if(bounty > 0){
            bossbarTitle += Messages.get("bounty.bossbar-bounty").replace("%BOUNTY%", Integer.toString(kitPlayer.getBounty()));
        }

        bossBar.setName(_text(bossbarTitle));
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
        return PlayersRegistry.getInstance().getPlayers().stream()
                .sorted(new BountyComparator())
                .limit(10)
                .collect(Collectors.toList());
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
                                Messages.get("bounty.complete")
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
                                Messages.get("bounty.denied")
                                        .replace("%PLAYER%", killed.getMcPlayer().getName())
                                        .replace("%BOUNTY%", killed.getBounty()/2 + "")
                        ));
                    }
            );
        }

        killed.setBounty(0);
    }
}
