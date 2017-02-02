package com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors;

import com.mishkapp.minecraft.plugins.squarekit.KitRegistry;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.*;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.MathUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
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
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class EventInterceptor {


    public EventInterceptor(){
        Sponge.getGame().getServer().getOnlinePlayers().
                forEach((p -> SquareKit.getPlayersRegistry().registerPlayer(p.getUniqueId())));

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
        if(event.getTargetEntity() instanceof ItemFrame){
            event.setCancelled(true);
            return;
        }
        ItemStack mainHand = player.getItemInHand(MAIN_HAND).orElse(null);
        ItemStack offHand = player.getItemInHand(OFF_HAND).orElse(null);

        if(mainHand == null &&
                offHand == null){
            return;
        }
        if(offHand == null &&
                event instanceof InteractBlockEvent.Secondary.OffHand){
            return;
        }

        if(mainHand == null &&
                event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        if(offHand != null && event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        ItemStack usedItem;
        HandType handType;

        if (mainHand != null &&
                offHand != null){
            usedItem = offHand;
            handType = OFF_HAND;
        } else {
            if(mainHand != null){
                usedItem = mainHand;
                handType = MAIN_HAND;
            } else {
                usedItem = offHand;
                handType = OFF_HAND;
            }
        }

        if(Utils.isKitItem(usedItem)){
            Sponge.getEventManager().post(new ItemUsedOnTargetEvent(SquareKit.getPlayersRegistry().getPlayer(player), handType, event.getTargetEntity()));
        }
    }

    @Listener
    public void onEntityInterractMain(InteractEntityEvent.Primary event, @First Player player){
        if(event.getTargetEntity() instanceof ItemFrame){
            event.setCancelled(true);
            return;
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
        if(offHand == null &&
                event instanceof InteractBlockEvent.Secondary.OffHand){
            return;
        }

        if(mainHand == null &&
                event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        if(offHand != null && event instanceof InteractBlockEvent.Secondary.MainHand){
            return;
        }

        ItemStack usedItem;
        HandType handType;

        if (mainHand != null &&
                offHand != null){
            usedItem = offHand;
            handType = OFF_HAND;
        } else {
            if(mainHand != null){
                usedItem = mainHand;
                handType = MAIN_HAND;
            } else {
                usedItem = offHand;
                handType = OFF_HAND;
            }
        }

        if(Utils.isKitItem(usedItem)){
            Entity target = getTarget(event, player);
            if(target != null){
                Sponge.getEventManager().post(new ItemUsedOnTargetEvent(SquareKit.getPlayersRegistry().getPlayer(player), handType, target));
            } else {
                Sponge.getEventManager().post(new ItemUsedEvent(SquareKit.getPlayersRegistry().getPlayer(player), handType));
                
            }
        }
    }

    private Entity getTarget(InteractBlockEvent.Secondary event, Player player) {
        List<Entity> list = player.getNearbyEntities(100).stream().filter(e -> {
            if(e == player || !(e instanceof Living)){
                return false;
            }

            double x0 = player.getLocation().getX();
            double y0 = player.getLocation().getY();
            double z0 = player.getLocation().getZ();

            double x = e.getLocation().getX() - x0;
            double y = e.getLocation().getY() - y0;
            double z = e.getLocation().getZ() - z0;

            double r = Math.sqrt((x * x) + (y * y) + (z * z));

            double phi = Math.acos(z / r);
            phi = Math.toDegrees(phi);

            double theta = Math.acos(y / r);
            theta = Math.toDegrees(theta);
            theta = theta - 90.0;

            if(x < 0){
                phi = phi - 360.0;
            } else {
                phi = phi * (-1);
            }

            double pTheta = player.getHeadRotation().getX();
            double pPhi = player.getHeadRotation().getY();

            return MathUtils.isSameDirection(phi, pPhi, theta, pTheta, 12);
        }).sorted(Comparator.comparingDouble(e2 -> e2.getLocation().getPosition().distance(player.getLocation().getPosition()))).collect(Collectors.toList());
        if(list.size() == 0){
            return null;
        } else {
            return list.get(0);
        }
    }

    @Listener
    public void onEntityCollideEntity(CollideEntityEvent.Impact event, @First final Projectile entity){
        UUID creatorId = entity.getCreator().orElse(null);
        if(creatorId == null){
            return;
        }


        List<Entity> entities = event.getEntities();
        if(entities.stream().filter(e -> e.getUniqueId().equals(creatorId)).count() > 0){
            event.setCancelled(true);
            return;
        }

        KitPlayer tempPlayer = SquareKit.getPlayersRegistry().getPlayer(entity.getCreator().orElse(null));

        if(tempPlayer == null){
            return;
        }

        final KitPlayer kitPlayer = tempPlayer;
        entities.forEach(e -> Sponge.getEventManager().post(new EntityCollideEntityEvent(kitPlayer, entity, e)));
    }

    @Listener
    public void onEntityCollideEntity(CollideEntityEvent event, @First final Entity entity){
        if(entity instanceof Projectile){
            return;
        }
        UUID creatorId = entity.getCreator().orElse(null);
        if(creatorId == null){
            return;
        }


        List<Entity> entities = event.getEntities();
        if(entities.stream().filter(e -> e.getUniqueId().equals(creatorId)).count() > 0){
            event.setCancelled(true);
            return;
        }

        KitPlayer tempPlayer = SquareKit.getPlayersRegistry().getPlayer(entity.getCreator().orElse(null));

        if(tempPlayer == null){
            return;
        }

        final KitPlayer kitPlayer = tempPlayer;
        entities.forEach(e -> Sponge.getEventManager().post(new EntityCollideEntityEvent(kitPlayer, entity, e)));
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
        event.setCancelled(true);
    }

    @Listener
    private void onFoodEated(UseItemStackEvent event){
//        System.out.println("event = " + event);
//        System.out.println("cause = " + event.getCause().all());
    }

    @Listener
    public void onPlayerUpdateRequest(PlayerUpdateRequestEvent event){
        if(event.getPlayer() != null){
            event.getPlayer().update();
        }
    }

    @Listener
    public void onAuth(ClientConnectionEvent.Auth event){
            SquareKit.getPlayersRegistry().registerPlayer(event.getProfile().getUniqueId());
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Login event){
        if(!SquareKit.getInstance().isInitialized()){
            event.setMessage(Text.of("Сервер еще запускается, подождите"));
            event.setCancelled(true);
        }
        UUID uuid = event.getProfile().getUniqueId();
        if(PlayersRegistry.getInstance().hasDummy(uuid)){
            event.setMessage(Text.of("Вы слишком часто перезаходите"));
            event.setCancelled(true);
        }
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @First Player player){
        event.setMessageCancelled(true);
        PlayersRegistry.getInstance().initPlayer(player);
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
        String kitId = kitPlayer.getCurrentKit().getId();
        KitRegistry.getInstance().getKit(kitId).applyToPlayer(kitPlayer);
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event, @First Player player){
        event.setMessageCancelled(true);
        SquareKit.getPlayersRegistry().prepareForUnregistrationPlayer(player);
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
        return PlayersRegistry.getInstance().getPlayer(player).isInBuildMode();
    }
}
