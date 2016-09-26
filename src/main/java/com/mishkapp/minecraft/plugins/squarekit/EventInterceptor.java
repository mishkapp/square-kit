package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.events.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.impl.AbstractAttackEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;

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
        SquareKit.getPlayersRegistry().tickAllPlayers();
    }

    @Listener
    public void onKitEvent(KitEvent event){
        event.getPlayer().handleEvent(event);
    }

    @Listener
    public void onEntityDamage(DamageEntityEvent event){
        //TODO: rework this
//        int damageMultiplier = 5;
//        switch (event.getCause()){
//            case CONTACT:
//            case SUFFOCATION:
//            case FALL:
//            case FIRE:
//            case FIRE_TICK:
//            case MELTING:
//            case LAVA:
//            case DROWNING:
//            case LIGHTNING:
//            case ENTITY_EXPLOSION:
//            case BLOCK_EXPLOSION:
//            case STARVATION:
//            case WITHER:
//            case MAGIC:
//            case FALLING_BLOCK:
//            case THORNS:
//            case FLY_INTO_WALL:
//                event.setDamage(event.getDamage() * damageMultiplier);
//        }
    }

    @Listener
    public void onEntityInterract(InteractEntityEvent.Secondary event, @First Player player){
        ItemStack mainHand = player.getItemInHand(MAIN_HAND).orElse(null);
        ItemStack offHand = player.getItemInHand(OFF_HAND).orElse(null);

        if(mainHand == null &&
                offHand == null){
            return;
        }

        if(offHand == null && event instanceof InteractBlockEvent.Secondary.OffHand){
            return;
        }

        if(mainHand == null && event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        if(mainHand != null && offHand != null && event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        ItemStack usedItem;

        if (mainHand != null && offHand != null){
            usedItem = offHand;
        } else {
            if(mainHand != null){
                usedItem = mainHand;
            } else {
                usedItem = offHand;
            }
        }

        if(Utils.isKitItem(usedItem)){
            Sponge.getEventManager().post(new ItemUsedEvent(SquareKit.getPlayersRegistry().getPlayer(player.getUniqueId())));
        }
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event, @First Player player){
        ItemStack mainHand = player.getItemInHand(MAIN_HAND).orElse(null);
        ItemStack offHand = player.getItemInHand(OFF_HAND).orElse(null);

        if(mainHand == null &&
                offHand == null){
            return;
        }

        if(offHand == null && event instanceof InteractBlockEvent.Secondary.OffHand){
            return;
        }

        if(mainHand == null && event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        if(mainHand != null && offHand != null && event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        ItemStack usedItem;

        if (mainHand != null && offHand != null){
            usedItem = offHand;
        } else {
            if(mainHand != null){
                usedItem = mainHand;
            } else {
                usedItem = offHand;
            }
        }

        if(Utils.isKitItem(usedItem)){
            Sponge.getEventManager().post(new ItemUsedEvent(SquareKit.getPlayersRegistry().getPlayer(player.getUniqueId())));
        }
    }


    @Listener
    public void onPickup(ChangeInventoryEvent event, @First Player player){
        //TODO: It updates VERY often, should rework it
        requestUpdate(player.getUniqueId());
    }

    @Listener
    public void onPlayerUpdateRequest(PlayerUpdateRequestEvent event){
        if(event.getPlayer() != null){
            event.getPlayer().update();
        }
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join event){
            SquareKit.getPlayersRegistry().registerPlayer(event.getTargetEntity());
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event){
        SquareKit.getPlayersRegistry().unregisterPlayer(event.getTargetEntity());
    }

    @Listener
    public void onHit(AbstractAttackEntityEvent event){
        if(event.getTargetEntity() instanceof Player){
            KitPlayer kPlayer = SquareKit.getPlayersRegistry().getPlayer(event.getTargetEntity().getUniqueId());
            event.setBaseOutputDamage(event.getBaseOutputDamage() * (1.0 - kPlayer.getPhysicalResist()));
        }

    }

    //TODO: arrow events
//    @Listener
//    public void onArrowLaunch(EntityShootBowEvent event) {
//        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
//            Player player = (Player)event.getEntity();
//            Bukkit.getServer().getPluginManager().callEvent(new ArrowLaunchEvent(
//                    SquareKit.getPlayersRegistry().getPlayer(player.getUniqueId()), (Arrow)event.getProjectile()));
//        }
//    }
//
//    @Listener
//    public void onArrowHit(EntityDamageByEntityEvent event) {
//        if (event.getDamager() instanceof Arrow) {
//
//            Arrow arrow = (Arrow)event.getDamager();
//            if (arrow.getShooter() instanceof Player) {
//
//                KitPlayer player = SquareKit.getPlayersRegistry().getPlayer(((Player)arrow.getShooter()).getUniqueId());
//                Bukkit.getServer().getPluginManager().callEvent(new ArrowHitEntityEvent(player, arrow,
//                        SquareKit.getPlayersRegistry().getPlayer((event.getEntity()).getUniqueId())));
//                SquareKit.getPlayersRegistry().getPlayer((event.getEntity()).getUniqueId()).addPhysicalDamage(event.getDamage());
//                event.setDamage(0);
//            }
//
//        }
//    }

    private void requestUpdate(UUID uuid) {
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(1).
                execute(() -> {
                    Sponge.getEventManager().post(new PlayerUpdateRequestEvent(SquareKit.getPlayersRegistry().getPlayer(uuid)));
                }).
                submit(SquareKit.getInstance());
    }
}
