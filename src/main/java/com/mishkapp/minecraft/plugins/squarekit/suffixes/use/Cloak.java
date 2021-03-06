package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mishkapp on 14.12.2016.
 */
public class Cloak extends UseSuffix {

    private PotionEffect effect;

    private Vector3d invisPosition = null;

    private double duration = 60.0;

    public Cloak(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            duration = Integer.parseInt(args[2]);
        }

        effect = PotionEffect.builder()
                .potionType(PotionEffectTypes.INVISIBILITY)
                .amplifier(1)
                .duration((int) (duration * 20))
                .build();
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof SuffixTickEvent){
            if(invisPosition == null){
                return;
            }

            if(kitPlayer.getMcPlayer().getLocation().getPosition().distance(invisPosition) > 0.5){
                invisPosition = null;
                List<PotionEffect> effects = kitPlayer.getMcPlayer().get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
                effects = effects.stream().filter(e -> !e.getType().equals(PotionEffectTypes.INVISIBILITY)).collect(Collectors.toList());
                kitPlayer.getMcPlayer().offer(Keys.POTION_EFFECTS, effects);
            }
        }
    }

    @Override
    protected void onUse() {
        List<PotionEffect> effects = kitPlayer.getMcPlayer().get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
        effects.add(effect);
        kitPlayer.getMcPlayer().offer(Keys.POTION_EFFECTS, effects);

        invisPosition = kitPlayer.getMcPlayer().getLocation().getPosition();
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.cloak")
                .replace("%TIME%", FormatUtils.unsignedRound(duration))
                + super.getLoreEntry();
    }
}
