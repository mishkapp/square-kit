package com.mishkapp.minecraft.plugins.squarekit.suffixes.passive;

import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
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

/**
 * Created by mishkapp on 08.12.2016.
 */
public class AstralVision extends Suffix {

    private double evasion = 0.3;
    private double speed = 0.2;
    private double mpRegen = 0.125;
    private double hpRegen = 0.05;

    private int drawDelay = 8;
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

    private void drawAreas() {
        List<Area> areas = AreaRegistry.getInstance().getNearbyAreas(kitPlayer.getMcPlayer(), 30);
        if (areas.size() == 0) {
            return;
        }

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

    @Override
    public String getLoreEntry() {
        return Messages.get("astral-vision-suffix")
                .replace("%HPREGEN%", FormatUtils.tenth(hpRegen * 4))
                .replace("%MPREGEN%", FormatUtils.tenth(mpRegen * 4))
                .replace("%SPEED%", FormatUtils.round(speed * 100))
                .replace("%EVASION%", FormatUtils.round(evasion * 100));
    }
}
