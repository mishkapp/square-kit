package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.effects.Effect;
import com.mishkapp.minecraft.plugins.squarekit.effects.Flame;
import com.mishkapp.minecraft.plugins.squarekit.events.*;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils.getTaskBuilder;
import static java.lang.Math.*;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class FireShield extends UseSuffix {
    private long duration;
    private boolean isActive = false;
    private ParticleEffect particleEffect;
    private Random random = new Random();

    private int time = 10;

    public FireShield(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        duration = 30 * 20;
        cooldown = 90 * 1000;
        manaCost = 60 - (level * 64.0/60);

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
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }


            double currentMana = kitPlayer.getCurrentMana();

            if(currentMana < manaCost){
                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.get("nomana")));
                return;
            }
            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();

            kitPlayer.setCurrentMana(currentMana - manaCost);

            isActive = true;

            getTaskBuilder().execute(() -> {
                isActive = false;
            }).
                    delayTicks(duration).
                    submit(SquareKit.getInstance());
        }
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
        KitPlayer attacker = event.getPlayer();

        List<Effect> effects = attacker.getEffects();

        List<Effect> flames = effects.parallelStream().filter(e -> e instanceof Flame && e.isRunning()).collect(Collectors.toList());

        if(flames.size() > 0){
            Flame flame = (Flame) flames.get(0);
            flame.setRunning(false);
            attacker.addEffect(new Flame(attacker, this, Math.min(flame.getLevel() + 1, 5), time * 1000));
        } else {
            attacker.addEffect(new Flame(attacker, this, 1, time * 1000));
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("fire-shield-suffix")
                .replace("%TIME%", FormatUtils.unsignedTenth(duration/20))
                + super.getLoreEntry();
    }
}
