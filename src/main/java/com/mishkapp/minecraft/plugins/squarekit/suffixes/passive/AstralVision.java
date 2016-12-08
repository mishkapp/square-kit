package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.WarpZonesRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class AstralVision extends Suffix {

    private double evasion = 0.3;
    private double speed = 0.2;
    private double mpRegen = 0.125;
    private double hpRegen = 0.05;

    private int drawDelay = 6;
    private int currentTick = 0;
    private Random random = new Random();

    private ParticleEffect effect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.END_ROD)
            .build();

    public AstralVision(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            if(!isItemHolding()){
                return;
            }
            if(AreaRegistry.getInstance().getApplicableAreas(kitPlayer.getMcPlayer()).size() > 0){
                kitPlayer.getSpeedAdds().put(this, speed);
                kitPlayer.getEvasionAdds().put(this, evasion);
                kitPlayer.getManaRegenAdds().put(this, mpRegen);
                kitPlayer.getHealthRegenAdds().put(this, hpRegen);
                drawEffect();
            } else {
                kitPlayer.getSpeedAdds().put(this, 0.0);
                kitPlayer.getEvasionAdds().put(this, 0.0);
                kitPlayer.getManaRegenAdds().put(this, 0.0);
                kitPlayer.getHealthRegenAdds().put(this, 0.0);
            }
            kitPlayer.updateStats();

            if(currentTick < drawDelay){
                currentTick += 1;
            } else {
                drawAreas();
                currentTick = 0;
            }
        }
    }

    private void drawEffect(){

        for(double i = 0; i < 2 * PI; i += random.nextDouble() * 2){
            Vector3d offset = new Vector3d(
                    random.nextGaussian()/5,
                    random.nextDouble(),
                    random.nextGaussian()/5
            );

            Vector3d velocity = new Vector3d(
                    random.nextGaussian()/25,
                    0.2 + random.nextGaussian()/50,
                    random.nextGaussian()/25
            );
            ParticleEffect pe = ParticleEffect.builder().from(effect).offset(offset).velocity(velocity).build();

            kitPlayer.getMcPlayer().getWorld().spawnParticles(
                    pe,
                    kitPlayer.getMcPlayer().getLocation().getPosition().add(
                            sin(i) + random.nextGaussian() / 15,
                            0,
                            cos(i) + random.nextGaussian() / 15));

        }


    }

    private void drawAreas() {
        List<Area> areas = AreaRegistry.getInstance().getNearbyAreas(kitPlayer.getMcPlayer(), 30);
        if (areas.size() > 0) {
            for (Area area : areas) {
                area.getBoundPoints().stream()
                        .filter(p ->
                                (random.nextDouble() < 0.3
                                        && kitPlayer.getMcPlayer().getLocation().getPosition().distance(p) < 30))
                        .forEach(p ->
                                kitPlayer.getMcPlayer().spawnParticles(
                                        effect,
                                        p.add(
                                                random.nextGaussian() / 4,
                                                random.nextGaussian() / 4,
                                                random.nextGaussian() / 4)));
            }
        }

        List<WarpZonesRegistry.WarpPoint> points = WarpZonesRegistry.getInstance().getNearbyPoints(kitPlayer.getMcPlayer(), 30);
        points.forEach(p -> {
            if(p == null){
                return;
            }
            for (double i = 0; i < PI * 2; i += 0.5) {
                kitPlayer.getMcPlayer().spawnParticles(
                        effect,
                        p.getPosition().add(
                                0.7 * sin(i) + random.nextGaussian() / 10,
                                1 + random.nextGaussian() / 10,
                                0.7 * cos(i) + random.nextGaussian() / 10));
            }
        });




    }

    @Override
    public String getLoreEntry() {
        return Messages.get("astral-vision-suffix")
                .replace("%HPREGEN%", FormatUtils.tenth(hpRegen * 4))
                .replace("%MPREGEN%", FormatUtils.tenth(mpRegen * 4))
                .replace("%SPEED%", FormatUtils.round(speed * 100))
                .replace("%EVASION%", FormatUtils.round(evasion * 100));
    }
}
