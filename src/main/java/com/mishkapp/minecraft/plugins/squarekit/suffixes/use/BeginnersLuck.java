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
 * Created by mishkapp on 31.10.2016.
 */
public class BeginnersLuck extends UseSuffix {
    private boolean isActive = false;
    private ParticleEffect particleEffect;
    private Random random = new Random();

    private double duration = 30.0;
    private double evasion = 0.1;
    private double critChance = 0.1;

    public BeginnersLuck(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            duration = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            evasion = Double.parseDouble(args[3]);
        }
        if(args.length > 4){
            critChance = Double.parseDouble(args[4]);
        }
        particleEffect = ParticleEffect.builder()
                .type(ParticleTypes.MAGIC_CRITICAL_HIT)
                .quantity(2)
                .offset(new Vector3d(0, 1, 0))
                .build();
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
        HashMap<Suffix, Double> evaAdds = kitPlayer.getEvasionAdds();
        HashMap<Suffix, Double> crChAdds = kitPlayer.getCriticalChanceAdds();

        evaAdds.put(this, evasion);
        crChAdds.put(this, critChance);
        kitPlayer.updateStats();

        Sponge.getScheduler().createTaskBuilder()
                .execute(t -> {
                    isActive = false;
                    evaAdds.put(this, 0.0);
                    crChAdds.put(this, 0.0);
                    kitPlayer.updateStats();
                })
                .delayTicks((long) (duration * 20))
                .submit(SquareKit.getInstance());
    }

    private void addEffect(){
        Player player = kitPlayer.getMcPlayer();
        World world = player.getWorld();
        for (int i = 0; i < 2; i++){
            world.spawnParticles(
                    particleEffect,
                    player.getLocation().getPosition().add(random.nextGaussian()/3 + 0.1, random.nextGaussian(), random.nextGaussian()/3 + 0.1)
            );
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("beginners-luck-suffix")
                .replace("%EVA%", FormatUtils.round(evasion * 100))
                .replace("%CRCH%", FormatUtils.round(critChance * 100))
                .replace("%TIME%", FormatUtils.unsignedRound(duration))
                + super.getLoreEntry();
    }
}
