package com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.TickBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.world.ExplosionEvent;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class WorldChangeListener {
    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @First Player player){
        if (!isInBuildMode(player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player){
        if (!isInBuildMode(player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent event, @First Player player){
        if (!isInBuildMode(player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockDecay(ChangeBlockEvent.Decay event){
        event.setCancelled(true);
    }

    @Listener
    public void onBlockTick(TickBlockEvent event){
        event.setCancelled(true);
    }

    @Listener
    public void onExplode(ExplosionEvent.Pre event){
        event.setCancelled(true);
    }

    private boolean isInBuildMode(Player player){
        return PlayersRegistry.getInstance().getPlayer(player).isInBuildMode();
    }
}
