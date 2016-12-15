package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedOnTargetEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

import static com.mishkapp.minecraft.plugins.squarekit.utils.ItemUtils.clearCopy;

/**
 * Created by mishkapp on 14.12.2016.
 */
public class Disguise extends UseSuffix {

    public Disguise(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        cooldown = 30 * 1000;
        manaCost = 10;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof ItemUsedOnTargetEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(!(((ItemUsedOnTargetEvent) event).getTarget() instanceof Player)){
                return;
            }

            if(AreaRegistry.getInstance().isInSafeArea(player)){
                return;
            }

            if(((ItemUsedOnTargetEvent) event).getTarget().getLocation().getPosition().distance(player.getLocation().getPosition()) > 5){
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

            KitPlayer target = PlayersRegistry.getInstance().getPlayer(((ItemUsedOnTargetEvent) event).getTarget().getUniqueId());

            kitPlayer.getMcPlayer().setHelmet(clearCopy(target.getMcPlayer().getHelmet().orElse(null)));
            kitPlayer.getMcPlayer().setChestplate(clearCopy(target.getMcPlayer().getChestplate().orElse(null)));
            kitPlayer.getMcPlayer().setLeggings(clearCopy(target.getMcPlayer().getLeggings().orElse(null)));
            kitPlayer.getMcPlayer().setBoots(clearCopy(target.getMcPlayer().getBoots().orElse(null)));
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("disguise-suffix")
                + super.getLoreEntry();
    }
}
