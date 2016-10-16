package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;
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
                .count(2)
                .offset(new Vector3d(0, 1, 0))
                .build();
    }

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
                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("nomana"));
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
        for (int i = 0; i < 16; i++){
            world.spawnParticles(
                    particleEffect,
                    player.getLocation().getPosition().add(random.nextGaussian()/3 + 0.1, random.nextGaussian(), random.nextGaussian()/3 + 0.1)
            );
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("shelter-suffix")
                .replace("%PRES%", FormatUtils.round(pRes * 100))
                .replace("%ATTACK%", FormatUtils.round(fallDamage))
                .replace("%TIME%", FormatUtils.tenth(duration/20))
                + super.getLoreEntry();
    }
}
