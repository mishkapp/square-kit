package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import static com.mishkapp.minecraft.plugins.squarekit.utils.ItemUtils.clearCopy;

/**
 * Created by mishkapp on 14.12.2016.
 */
public class Disguise extends TargetedSuffix {

    public Disguise(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected void onUse(Entity target) {
        if(!(target instanceof Player)){
            return;
        }
        Player player = (Player)target;
        kitPlayer.getMcPlayer().setHelmet(clearCopy(player.getHelmet().orElse(null)));
        kitPlayer.getMcPlayer().setChestplate(clearCopy(player.getChestplate().orElse(null)));
        kitPlayer.getMcPlayer().setLeggings(clearCopy(player.getLeggings().orElse(null)));
        kitPlayer.getMcPlayer().setBoots(clearCopy(player.getBoots().orElse(null)));
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.disguise")
                + super.getLoreEntry();
    }
}
