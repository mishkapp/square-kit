package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mishkapp on 14.12.2016.
 */
public class Cloak extends UseSuffix {

    private int time = 60;
    private PotionEffect effect = PotionEffect.builder()
            .potionType(PotionEffectTypes.INVISIBILITY)
            .amplifier(1)
            .duration(time * 20)
            .build();

    private Vector3d invisPosition = null;

    public Cloak(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 3 * 1000;
        manaCost = 10;
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

            List<PotionEffect> effects = kitPlayer.getMcPlayer().get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
            effects.add(effect);
            kitPlayer.getMcPlayer().offer(Keys.POTION_EFFECTS, effects);

            invisPosition = kitPlayer.getMcPlayer().getLocation().getPosition();

        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("cloak-suffix")
                .replace("%TIME%", FormatUtils.unsignedRound(time))
                + super.getLoreEntry();
    }
}
