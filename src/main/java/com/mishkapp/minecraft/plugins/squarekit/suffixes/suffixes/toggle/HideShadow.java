package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.toggle;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Toggle;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.util.Collections;
import java.util.Random;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class HideShadow extends Toggle {
    private boolean isActive = false;
    private double activationCost = 5;
    private double manaCost = 0.5;
    private ParticleEffect effect;
    private Random rnd = new Random();
    private Slot slot;

    public HideShadow(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        effect = ParticleEffect.builder()
                .type(ParticleTypes.SMOKE_LARGE)
                .count(4)
                .offset(new Vector3d(0, 1, 0))
                .build();
    }

    @Override
    protected boolean isItemPresent() {
        return false;
    }

    @Override
    public void register() {

    }

    @Override
    public void handle(KitEvent event) {
        Player player = kitPlayer.getMcPlayer();

        if(event instanceof ItemUsedEvent) {
            if(!isItemPresentInHand()){
                return;
            }

            if(isActive){
                deactivateItem();
                isActive = false;
            } else {
                double currentMana = kitPlayer.getCurrentMana();
                if(currentMana < activationCost){
                    return;
                }
                activateItem();
                kitPlayer.setCurrentMana(currentMana - activationCost);
            }
        }
        if(event instanceof SuffixTickEvent){
            if(!isActive){
                for(int i = 0; i < 4; i++){
                    player.spawnParticles(
                            effect,
                            player.getLocation().getPosition().add(rnd.nextGaussian()/4, rnd.nextGaussian()/4, rnd.nextGaussian()/4),
                            25);
                }
            } else {
                double currentMana = kitPlayer.getCurrentMana();
                if(currentMana < manaCost){
                    deactivateItem();
                }
                kitPlayer.setCurrentMana(currentMana - manaCost);
            }
        }
    }

    private void activateItem(){
        isActive = true;
        itemStack.offer(Keys.ITEM_ENCHANTMENTS, Collections.EMPTY_LIST);
        updateSlot();
    }

    private void deactivateItem(){
        isActive = false;
        itemStack.remove(EnchantmentData.class);
        updateSlot();
    }

    private void updateSlot(){
        slot = findSlot();
        slot.set(itemStack);
    }

    private Slot findSlot(){
        for(Object o : kitPlayer.getMcPlayer().getInventory().slots()){
            Slot slot = (Slot)o;
            ItemStack i = slot.peek().orElse(null);
            if(isSimilar(itemStack, i)){
                return slot;
            }
        }
        return kitPlayer.getMcPlayer().getInventory().first();
    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-hide-shadow")
                .replace("%ACTIVATION_COST%", Formatters.round.format(activationCost))
                .replace("%MANA_COST%", Formatters.tenth.format(manaCost * 4));
    }
}
