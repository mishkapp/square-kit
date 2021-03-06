package com.mishkapp.minecraft.plugins.squarekit.suffixes.active;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by mishkapp on 03.10.2016.
 */
public class HideShadow extends Suffix {
    private double activationCost = 10;
    private double manaCost = 0.5;
    private double regenerationBonus = 0.7;

    private boolean isActive = false;
    private ParticleEffect effect;
    private Random rnd = new Random();
    private Slot slot;

    public HideShadow(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            activationCost = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            manaCost = Double.parseDouble(args[1]);
        }
        if(args.length > 2){
            regenerationBonus = Double.parseDouble(args[2]);
        }

        effect = ParticleEffect.builder()
                .type(ParticleTypes.LARGE_SMOKE)
                .quantity(4)
                .offset(new Vector3d(0, 1, 0))
                .build();
    }

    @Override
    public void register() {
        deactivateItem();
    }

    @Override
    public void handle(KitEvent event) {
        Player player = kitPlayer.getMcPlayer();

        if(event instanceof ItemUsedEvent) {
            if(!isItemInHand()){
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
                    player.getWorld().spawnParticles(
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
        List<ItemEnchantment> enchantments = new ArrayList<>();
        enchantments.add(new ItemEnchantment(Enchantments.INFINITY, 1));
        itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
        itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);
        HashMap<Suffix, Double> adds = kitPlayer.getHealthRegenAdds();
        adds.put(this, regenerationBonus / 4);
        updateSlot();
    }

    private void deactivateItem(){
        isActive = false;
        itemStack.remove(EnchantmentData.class);
        HashMap<Suffix, Double> adds = kitPlayer.getHealthRegenAdds();
        adds.remove(this);
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
        return Messages.get("suffix.hide-shadow")
                .replace("%ACTIVATION_COST%", FormatUtils.unsignedRound(activationCost))
                .replace("%REGENERATION_BONUS%", FormatUtils.tenth(regenerationBonus))
                .replace("%MANA_COST%", FormatUtils.unsignedTenth(manaCost * 4));
    }
}
