package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedOnTargetEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by mishkapp on 07.01.2017.
 */
public abstract class TargetedSuffix extends SpellSuffix {
    protected double distance = 30.0;

    public TargetedSuffix(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            distance = Double.parseDouble(args[2]);
        }
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof ItemUsedOnTargetEvent){
            Player player = kitPlayer.getMcPlayer();

            if(kitPlayer.isInSafeZone()){
                return;
            }

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(((ItemUsedOnTargetEvent) event).getTarget().getLocation().getPosition().distance(player.getLocation().getPosition()) > distance){
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

            onUse(((ItemUsedOnTargetEvent) event).getTarget());
        }
    }

    protected abstract void onUse(Entity target);
}
