package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class AlphaBugs extends UseSuffix {
    private boolean isActive = false;
    private ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.FOOTSTEP)
            .quantity(4)
            .offset(new Vector3d(0, 0, 0))
            .build();

    private Random random = new Random();

    private double duration = 10.0;
    private double speed = 1.0;

    public AlphaBugs(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            duration = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            speed = Double.parseDouble(args[3]);
        }
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof SuffixTickEvent){
            if(isActive){
                addEffect();
            }
        }
    }

    @Override
    protected void onUse() {
        isActive = true;

        HashMap<Suffix, Double> speedAdds = kitPlayer.getSpeedAdds();
        HashMap<Suffix, Double> regenAdds = kitPlayer.getHealthRegenAdds();

        int nearbyPlayers = (int) kitPlayer.getMcPlayer().getNearbyEntities(20)
                .stream()
                .filter(e -> e instanceof Player)
                .count();

        speedAdds.put(this, speed);
        regenAdds.put(this, (nearbyPlayers * 0.2) * kitPlayer.getHealthRegen());
        kitPlayer.updateStats();

        Sponge.getScheduler().createTaskBuilder()
                .execute(t -> {
                    isActive = false;
                    speedAdds.put(this, 0.0);
                    regenAdds.put(this, 0.0);
                    kitPlayer.updateStats();
                })
                .delayTicks((long) (duration * 20))
                .submit(SquareKit.getInstance());
    }

    private void addEffect(){
        Player player = kitPlayer.getMcPlayer();
        World world = player.getWorld();
        for (int i = 0; i < 16; i++){
            world.spawnParticles(
                    particleEffect,
                    player.getLocation().getPosition().add(random.nextGaussian()/3 + 0.1, random.nextGaussian(), random.nextDouble()/3 + 0.1)
            );
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.alpha-bugs")
                .replace("%SPEED%", FormatUtils.unsignedRound(speed * 100))
                .replace("%TIME%", FormatUtils.unsignedTenth(duration))
                + super.getLoreEntry();
    }
}
