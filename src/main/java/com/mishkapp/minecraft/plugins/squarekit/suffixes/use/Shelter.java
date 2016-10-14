package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.SpongeUtils.getTaskBuilder;

/**
 * Created by mishkapp on 08.10.2016.
 */
public class Shelter extends UseSuffix {
    private long duration;
    private boolean isActive = false;
    private ParticleEffect particleEffect;
    private Random random = new Random();

    private double pRes = 0.8;
    private double fallDamage = 3;

    public Shelter(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        duration = 5 * 20;
        cooldown = 45 * 1000;
        manaCost = 100 - (level * 64.0/100);

        particleEffect = ParticleEffect.builder()
                .type(ParticleTypes.SLIME)
                .count(5)
                .offset(new Vector3d(0, 1, 0))
                .build();
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            if(isActive){
                addEffect();
            }
        }
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand()){
                return;
            }


            double currentMana = kitPlayer.getCurrentMana();

            if(currentMana < manaCost){
                player.sendMessage(Text.of(Messages.get("nomana")));
                return;
            }
            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();

            kitPlayer.setCurrentMana(currentMana - manaCost);

            isActive = true;

            HashMap<Suffix, Double> dmgAdds = kitPlayer.getPhysicalDamageAdds();
            HashMap<Suffix, Double> pResAdds = kitPlayer.getPhysicalResistAdds();

            double damage = kitPlayer.getAttackDamage();

            dmgAdds.put(this, -1 * (damage - fallDamage));
            pResAdds.put(this, pRes);
            kitPlayer.updateStats();

            getTaskBuilder().execute(() -> {
                isActive = false;
                dmgAdds.put(this, 0.0);
                pResAdds.put(this, 0.0);
                kitPlayer.updateStats();}).
                    delayTicks(duration).
                    submit(SquareKit.getInstance());
        }
    }

    private void addEffect(){
        Player player = kitPlayer.getMcPlayer();
        World world = player.getWorld();
        for (int i = 0; i < 5; i++){
            world.spawnParticles(
                    particleEffect,
                    player.getLocation().getPosition().add(random.nextGaussian()/4, random.nextGaussian()/4, random.nextGaussian()/4)
            );
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("shelter-suffix")
                .replace("%PRES%", Formatters.round.format(pRes * 100))
                .replace("%ATTACK%", Formatters.round.format(fallDamage))
                .replace("%TIME%", Formatters.tenth.format(duration/20))
                .replace("%COOLDOWN%", Formatters.tenth.format(cooldown/1000))
                .replace("%MANACOST%", Formatters.round.format(manaCost));
    }
}