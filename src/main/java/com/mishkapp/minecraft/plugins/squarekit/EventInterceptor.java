package com.mishkapp.minecraft.plugins.squarekit;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.events.*;
import com.mishkapp.minecraft.plugins.squarekit.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.property.item.FoodRestorationProperty;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.*;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;
import static org.spongepowered.api.event.cause.entity.damage.DamageTypes.SWEEPING_ATTACK;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class EventInterceptor {

    private Random random = new Random();
    private ParticleEffect criticalEffect = ParticleEffect.builder()
            .type(ParticleTypes.CRIT)
            .count(6)
            .offset(new Vector3d(0, 1, 0))
            .build();

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
    public void onPlayerRespawn(RespawnPlayerEvent event){
        KitPlayer kitPlayer = SquareKit.getPlayersRegistry().getPlayer(event.getTargetEntity().getUniqueId());
        kitPlayer.forceUpdate();
    }

    @Listener
    public void onHit(AttackEntityEvent event, @First EntityDamageSource damageSource){
        if(damageSource.getSource() instanceof Player){
            KitPlayer damager = SquareKit.getPlayersRegistry().getPlayer(damageSource.getSource().getUniqueId());
            Entity damaged = event.getTargetEntity();

            event.setBaseOutputDamage(damager.getAttackDamage());

            DamageModifier critModifier = null;
            for (Tuple<DamageModifier, Function<? super Double, Double>> t : event.getModifiers()) {
                if(t.getFirst().getType().equals(DamageModifierTypes.CRITICAL_HIT)){
                    critModifier = t.getFirst();
                }
            }
            if(critModifier == null){
                critModifier = DamageModifier.builder()
                        .type(DamageModifierTypes.CRITICAL_HIT)
                        .cause(event.getCause())
                        .build();
            }
            boolean isHitCritical = random.nextDouble() <= damager.getCriticalChance();

            event.setOutputDamage(critModifier, d -> {
                if(isHitCritical){
                    return d * damager.getCriticalPower();
                } else {
                    return 0.0;
                }
            });

            if(isHitCritical){
                for(int i = 0; i < 5; i++){
                    damaged.getWorld().spawnParticles(
                            criticalEffect,
                            damaged.getLocation().getPosition().add(
                                    random.nextGaussian()/4 + 0.1,
                                    random.nextGaussian()/4 + 0.1,
                                    random.nextGaussian()/4 + 0.1));
                }
            }

            damaged.damage(event.getFinalOutputDamage(), DamageSource.builder().type(SWEEPING_ATTACK).bypassesArmor().build());
            event.setBaseOutputDamage(0);
            Sponge.getEventManager().post(new PlayerAttackEntityEvent(
                    damager,
                    damaged
            ));
        }
    }

    @Listener
    public void onEntityDamage(DamageEntityEvent event, @First DamageSource damageSource){
        if(damageSource instanceof IndirectEntityDamageSource){
            return;
        }
        int damageMultiplier = 5;
        if(damageSource.getType() == DamageTypes.CONTACT
                || damageSource.getType() == DamageTypes.SUFFOCATE
                || damageSource.getType() == DamageTypes.FALL
                || damageSource.getType() == DamageTypes.FIRE
                || damageSource.getType() == DamageTypes.DROWN
                || damageSource.getType() == DamageTypes.EXPLOSIVE
                || damageSource.getType() == DamageTypes.HUNGER
                || damageSource.getType() == DamageTypes.VOID
                ){
            event.setBaseDamage(event.getBaseDamage() * damageMultiplier);
        }

        if(event.getTargetEntity() instanceof Player){
            KitPlayer kitPlayer = SquareKit.getPlayersRegistry().getPlayer(event.getTargetEntity().getUniqueId());
            if(damageSource.isMagic()){
                kitPlayer.addMagicDamage(event.getBaseDamage());
            } else if (damageSource.isAbsolute()){
                kitPlayer.addPureDamage(event.getBaseDamage());
            } else {
                kitPlayer.addPhysicalDamage(event.getBaseDamage());
            }
            event.setBaseDamage(0);
        }
    }

    @Listener
    public void onArrowHit(DamageEntityEvent event, @First IndirectEntityDamageSource source){
        if (!(source.getSource() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow)source.getSource();
        if (source.getIndirectSource() instanceof Player) {
            Player player = (Player) source.getIndirectSource();
            KitPlayer kitPlayer = SquareKit.getPlayersRegistry().getPlayer(player.getUniqueId());
            Sponge.getEventManager().post(new ArrowHitEntityEvent(
                    kitPlayer,
                    arrow,
                    event.getTargetEntity(),
                    event.getBaseDamage() / 10.0));
            event.setBaseDamage(0);
            //TODO: REMOVE THIS SHIT
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityInterract(InteractEntityEvent.Secondary event, @First Player player){
        ItemStack mainHand = player.getItemInHand(MAIN_HAND).orElse(null);
        ItemStack offHand = player.getItemInHand(OFF_HAND).orElse(null);

        if(mainHand == null &&
                offHand == null){
            return;
        }

        if(offHand == null && event instanceof InteractEntityEvent.Secondary.OffHand){
            return;
        }

        if(mainHand == null && event instanceof InteractEntityEvent.Secondary.MainHand){
            return;
        }

        if(mainHand != null && offHand != null && event instanceof InteractEntityEvent.Secondary.MainHand){
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
    public void onEntityCollideEntity(CollideEntityEvent.Impact event, @First final Entity entity){
        KitPlayer tempPlayer = SquareKit.getPlayersRegistry().getPlayer(entity.getCreator().orElse(null));

        if(tempPlayer == null){
            return;
        }

        final KitPlayer kitPlayer = tempPlayer;
        List<Entity> entities = event.getEntities();
        if(entities.contains(kitPlayer.getMcPlayer())){
            return;
        }
        entities.forEach(e -> Sponge.getEventManager().post(new EntityCollideEntityEvent(kitPlayer, entity, e)));
    }

    @Listener
    public void onEntityCollideBlock(CollideBlockEvent event){

    }

    @Listener
    @Exclude({ChangeInventoryEvent.Held.class, ChangeInventoryEvent.Transfer.class})
    public void onInventoryChange(ChangeInventoryEvent event, @First Player player){
        requestUpdate(player.getUniqueId());
    }

    //TODO: not working
    @Listener
    public void onDrop(DropItemEvent event, @First Player player){
        requestUpdate(player.getUniqueId());
    }

    @Listener
    public void onPlayerUpdateRequest(PlayerUpdateRequestEvent event){
        if(event.getPlayer() != null){
            event.getPlayer().update();
        }
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join event, @First Player player){
        SquareKit.getPlayersRegistry().registerPlayer(player);
        requestUpdate(player.getUniqueId());
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
                execute(() -> {
                    Sponge.getEventManager().post(new PlayerUpdateRequestEvent(SquareKit.getPlayersRegistry().getPlayer(uuid)));
                }).
                submit(SquareKit.getInstance());
    }
}
