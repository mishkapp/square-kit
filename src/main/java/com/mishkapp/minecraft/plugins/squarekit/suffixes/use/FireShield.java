package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.effects.Effect;
import com.mishkapp.minecraft.plugins.squarekit.effects.Flame;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackedByEntity;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.*;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class FireShield extends UseSuffix {
    private boolean isActive = false;
    private ParticleEffect particleEffect;
    private Random random = new Random();

    private double duration = 5.0;
    private long time = 10;

    public FireShield(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if (args.length > 2) {
            duration = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            time = Long.parseLong(args[3]);
        }

        particleEffect = ParticleEffect.builder()
                .type(ParticleTypes.FLAME)
                .quantity(1)
                .offset(new Vector3d(0, 0, 0))
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
        if(event instanceof PlayerAttackedByEntity){
            if(isActive){
                onAttack((PlayerAttackedByEntity) event);
            }
        }
    }

    @Override
    protected void onUse() {
        isActive = true;

        Sponge.getScheduler().createTaskBuilder()
                .execute(() -> isActive = false)
                .delayTicks((long) (duration * 20))
                .submit(SquareKit.getInstance());
    }

    private void addEffect(){
        Player player = kitPlayer.getMcPlayer();
        World world = player.getWorld();
        for (double i = 0; i < 2; i += 0.25){
            for (double j = 0; j < PI * 2; j += PI/4){
                world.spawnParticles(
                        particleEffect,
                        player.getLocation().getPosition().add(
                                sin(j + i) * cos(i - 1),
                                i,
                                cos(j + i) * cos(i - 1)
                ));
            }
        }
    }

    private void onAttack(PlayerAttackedByEntity event){
        if(!(event.getAttacker() instanceof Player)){
            return;
        }
        KitPlayer attacker = PlayersRegistry.getInstance().getPlayer(event.getAttacker().getUniqueId());

        List<Effect> effects = attacker.getEffects();

        List<Effect> flames = effects.parallelStream().filter(e -> e instanceof Flame && e.isRunning()).collect(Collectors.toList());

        if(flames.size() > 0){
            Flame flame = (Flame) flames.get(0);
            flame.setRunning(false);
            attacker.addEffect(new Flame(attacker, this, Math.min(flame.getLevel() + 1, 5), time * 50));
        } else {
            attacker.addEffect(new Flame(attacker, this, 1, time * 50));
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("fire-shield-suffix")
                .replace("%TIME%", FormatUtils.unsignedTenth(duration))
                + super.getLoreEntry();
    }
}
