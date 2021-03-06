package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by mishkapp on 08.01.2017.
 */
public abstract class UseSuffix extends SpellSuffix{
    public UseSuffix(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(kitPlayer.isInSafeZone()){
                return;
            }

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(!predicate()){
                return;
            }

            double currentMana = kitPlayer.getCurrentMana();

            if(currentMana < manaCost){
                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.get("alert.nomana")));
                return;
            }
            if(!isCooldowned(kitPlayer)){
                return;
            }

            lastUse = System.currentTimeMillis();

            kitPlayer.setCurrentMana(currentMana - manaCost);

            preUse();

            onUse();
        }
    }

    protected boolean predicate(){
        return true;
    }

    protected void preUse(){};

    protected abstract void onUse();
}
