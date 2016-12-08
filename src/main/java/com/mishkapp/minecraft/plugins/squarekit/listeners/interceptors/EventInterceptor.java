package com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors;

import com.mishkapp.minecraft.plugins.squarekit.KitRegistry;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.*;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.UUID;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class EventInterceptor {


    public EventInterceptor(){
        Sponge.getGame().getServer().getOnlinePlayers().
                forEach((p -> SquareKit.getPlayersRegistry().registerPlayer(p)));

    }

    @Listener
    public void onTick(TickEvent event){
        Sponge.getEventManager().post(new SuffixTickEvent(event.getPlayer()));
        event.getPlayer().tick();
    }

    @Listener
    public void onKitEvent(KitEvent event){
        event.getPlayer().handleEvent(event);
    }

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event){
        Sponge.getScheduler().createTaskBuilder()
                .delayTicks(5)
                .execute(r -> {
                    KitPlayer kitPlayer = SquareKit.getPlayersRegistry().getPlayer(event.getTargetEntity().getUniqueId());
                    KitRegistry.getInstance().getKit("recruit").applyToPlayer(kitPlayer);
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    @Listener
    public void onEntityInterract(InteractEntityEvent.Secondary event, @First Player player){
        ItemStack mainHand = player.getItemInHand(MAIN_HAND).orElse(null);
        ItemStack offHand = player.getItemInHand(OFF_HAND).orElse(null);

        if(mainHand.getItem().getId().equals("minecraft:air") &&
                offHand.getItem().getId().equals("minecraft:air")){
            return;
        }
        if(offHand.getItem().getId().equals("minecraft:air") &&
                event instanceof InteractEntityEvent.Secondary.OffHand){
            return;
        }

        if(mainHand.getItem().getId().equals("minecraft:air") &&
                event instanceof InteractEntityEvent.Secondary.MainHand){
            return;
        }

        if(!mainHand.getItem().getId().equals("minecraft:air") &&
                !offHand.getItem().getId().equals("minecraft:air") &&
                event instanceof InteractEntityEvent.Secondary.MainHand){
            return;
        }

        ItemStack usedItem;
        HandType handType;

        if (!mainHand.getItem().getId().equals("minecraft:air") &&
                !offHand.getItem().getId().equals("minecraft:air")){
            usedItem = offHand;
            handType = OFF_HAND;
        } else {
            if(!mainHand.getItem().getId().equals("minecraft:air")){
                usedItem = mainHand;
                handType = MAIN_HAND;
            } else {
                usedItem = offHand;
                handType = OFF_HAND;
            }
        }

        if(Utils.isKitItem(usedItem)){
            Sponge.getEventManager().post(new ItemUsedEvent(SquareKit.getPlayersRegistry().getPlayer(player.getUniqueId()), handType));
        }
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event, @First Player player){
        ItemStack mainHand = player.getItemInHand(MAIN_HAND).orElse(null);
        ItemStack offHand = player.getItemInHand(OFF_HAND).orElse(null);

        if(mainHand.getItem().getId().equals("minecraft:air") &&
                offHand.getItem().getId().equals("minecraft:air")){
            return;
        }
        if(offHand.getItem().getId().equals("minecraft:air") &&
                event instanceof InteractBlockEvent.Secondary.OffHand){
            return;
        }

        if(mainHand.getItem().getId().equals("minecraft:air") &&
                event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        if(!mainHand.getItem().getId().equals("minecraft:air") &&
                !offHand.getItem().getId().equals("minecraft:air") &&
                event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        ItemStack usedItem;
        HandType handType;

        if (!mainHand.getItem().getId().equals("minecraft:air") &&
                !offHand.getItem().getId().equals("minecraft:air")){
            usedItem = offHand;
            handType = OFF_HAND;
        } else {
            if(!mainHand.getItem().getId().equals("minecraft:air")){
                usedItem = mainHand;
                handType = MAIN_HAND;
            } else {
                usedItem = offHand;
                handType = OFF_HAND;
            }
        }

        if(Utils.isKitItem(usedItem)){
            Sponge.getEventManager().post(new ItemUsedEvent(SquareKit.getPlayersRegistry().getPlayer(player.getUniqueId()), handType));
        }
    }

    @Listener
    public void onEntityCollideEntity(CollideEntityEvent.Impact event, @First final Entity entity){
        KitPlayer tempPlayer = SquareKit.getPlayersRegistry().getPlayer(entity.getCreator().orElse(null));

        if(tempPlayer == null){
            return;
        }

        final KitPlayer kitPlayer = tempPlayer;
        List<Entity> entities = event.getEntities();
        if(entities.contains(kitPlayer.getMcPlayer())){
            event.setCancelled(true);
            return;
        }
        entities.forEach(e -> Sponge.getEventManager().post(new EntityCollideEntityEvent(kitPlayer, entity, e)));
    }

    @Listener
    public void onEntityCollideBlock(CollideBlockEvent event){

    }

    @Listener
    public void onInventoryClick(ClickInventoryEvent.Primary event, @First Player player){
        if (!isInBuildMode(player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInventoryClick(ClickInventoryEvent.Secondary event, @First Player player){
        if (!isInBuildMode(player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    private void onItemPickup(ChangeInventoryEvent.Pickup event){
        event.setCancelled(true);
    }

    @Listener
    public void onItemDrop(DropItemEvent event, @Root EntitySpawnCause escause) {
        if (escause.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerUpdateRequest(PlayerUpdateRequestEvent event){
        if(event.getPlayer() != null){
            event.getPlayer().update();
        }
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Login event, @First Player player){
        if(!player.hasPermission("squarekit.tester")){
            event.setMessage(TextSerializers.FORMATTING_CODE.deserialize("&6Эх, как жаль, что вы не тестер :("));
            event.setCancelled(true);
        }
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @First Player player){
        KitPlayer kitPlayer = SquareKit.getPlayersRegistry().registerPlayer(player);
        String kitId = kitPlayer.getCurrentKit();
        KitRegistry.getInstance().getKit(kitId).applyToPlayer(kitPlayer);
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event, @First Player player){
        SquareKit.getPlayersRegistry().unregisterPlayer(player);
    }

    //TODO: event not implemented
    @Listener
    public void onArrowLaunch(LaunchProjectileEvent event) {}

    private void requestUpdate(UUID uuid) {
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(1).
                execute(() -> Sponge.getEventManager().post(new PlayerUpdateRequestEvent(SquareKit.getPlayersRegistry().getPlayer(uuid)))).
                submit(SquareKit.getInstance());
    }

    private boolean isInBuildMode(Player player){
        return PlayersRegistry.getInstance().getPlayer(player.getUniqueId()).isInBuildMode();
    }
}
