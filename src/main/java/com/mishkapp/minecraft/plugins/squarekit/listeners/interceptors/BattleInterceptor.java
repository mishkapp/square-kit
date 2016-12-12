package com.mishkapp.minecraft.plugins.squarekit.listeners.interceptors;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.*;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.HarvestEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Tuple;

import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import static org.spongepowered.api.event.cause.entity.damage.DamageTypes.SWEEPING_ATTACK;

/**
 * Created by mishkapp on 30.10.2016.
 */
public class BattleInterceptor {

    private Random random = new Random();
    private ParticleEffect criticalEffect = ParticleEffect.builder()
            .type(ParticleTypes.CRITICAL_HIT)
            .quantity(6)
            .offset(new Vector3d(0, 1, 0))
            .build();

    @Listener
    public void onHit(AttackEntityEvent event, @First EntityDamageSource damageSource){
        if(damageSource.getSource() instanceof Player){
            if(AreaRegistry.getInstance().isInSafeArea((Player) damageSource.getSource())){
                event.setCancelled(true);
                return;
            }
            KitPlayer damager = SquareKit.getPlayersRegistry().getPlayer(damageSource.getSource().getUniqueId());
            Entity damaged = event.getTargetEntity();

            if(damaged instanceof Player){
                if(AreaRegistry.getInstance().isInSafeArea((Player) damaged)){
                    event.setCancelled(true);
                    return;
                }
                KitPlayer damagedPlayer = PlayersRegistry.getInstance().getPlayer(damaged.getUniqueId());
                if(damagedPlayer.getEvasion() >= random.nextDouble()){
                    event.setCancelled(true);
                    return;
                }
            }

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

            damaged.damage(
                    event.getFinalOutputDamage(),
                    EntityDamageSource.builder().entity(damager.getMcPlayer())
                            .type(SWEEPING_ATTACK)
                            .bypassesArmor().build());
            event.setBaseOutputDamage(0);
            Sponge.getEventManager().post(new PlayerAttackEntityEvent(
                    damager,
                    damaged
            ));
            if(damaged instanceof Player){
                Sponge.getEventManager().post(new PlayerAttackedByEntity(
                        PlayersRegistry.getInstance().getPlayer(damaged.getUniqueId()),
                        damager.getMcPlayer()
                ));
            }
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityDamage(DamageEntityEvent event, @First DamageSource damageSource){
        if(AreaRegistry.getInstance().isInSafeArea(event.getTargetEntity().getLocation())){
            event.setCancelled(true);
            return;
        }
        if(damageSource instanceof IndirectEntityDamageSource){
            return;
        }
        int damageMultiplier = 4;
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
            return;
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
            KitPlayer kitPlayer = SquareKit.getPlayersRegistry().getPlayer(player);
            Sponge.getEventManager().post(new ArrowHitEntityEvent(
                    kitPlayer,
                    arrow,
                    event.getTargetEntity(),
                    event.getBaseDamage() / 10.0));
            event.setBaseDamage(0);
            event.setCancelled(true);
        }
    }

    @Listener
    public void onHarvest(HarvestEntityEvent event){
        event.setCancelled(true);
        event.setExperience(0);
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Player player, @First DamageSource damageSource){
        event.setMessageCancelled(true);

        if(damageSource instanceof IndirectEntityDamageSource){
            IndirectEntityDamageSource ieds = (IndirectEntityDamageSource)damageSource;
            if(ieds.getIndirectSource() instanceof Player){
                Sponge.getEventManager().post(new PlayerKilledByPlayerEvent(
                        PlayersRegistry.getInstance().getPlayer(player),
                        PlayersRegistry.getInstance().getPlayer(ieds.getIndirectSource().getUniqueId())
                ));
            } else {
                Sponge.getEventManager().post(new PlayerKilledByEntityEvent(
                        PlayersRegistry.getInstance().getPlayer(player),
                        ieds.getIndirectSource()
                ));
            }
            return;
        }

        if(damageSource instanceof EntityDamageSource){
            EntityDamageSource eds = (EntityDamageSource)damageSource;
            if(eds.getSource() instanceof Player){
                Sponge.getEventManager().post(new PlayerKilledByPlayerEvent(
                        PlayersRegistry.getInstance().getPlayer(player),
                        PlayersRegistry.getInstance().getPlayer(eds.getSource().getUniqueId())
                ));
            } else {
                Sponge.getEventManager().post(new PlayerKilledByEntityEvent(
                        PlayersRegistry.getInstance().getPlayer(player),
                        eds.getSource()
                ));
            }
            return;
        }

        if(!damageSource.getType().equals(DamageTypes.CUSTOM)){
            Sponge.getEventManager().post(new PlayerKilledEvent(
                    PlayersRegistry.getInstance().getPlayer(player)
            ));
        }
    }

    @Listener
    public void onDummyDeath(DestructEntityEvent.Death event, @Getter("getTargetEntity") Human dummy, @First DamageSource damageSource){
        event.setMessageCancelled(true);
        UUID creator = dummy.getCreator().orElse(null);
        if(creator == null){
            return;
        }
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(creator);
        if(kitPlayer == null){
            return;
        }
        if(kitPlayer.getMcPlayer().isOnline()){
            return;
        }
        if(!PlayersRegistry.getInstance().hasDummy(dummy)){
            return;
        }

        if(damageSource instanceof IndirectEntityDamageSource){
            IndirectEntityDamageSource ieds = (IndirectEntityDamageSource)damageSource;
            if(ieds.getIndirectSource() instanceof Player){
                Sponge.getEventManager().post(new DummyKilledByPlayerEvent(
                        kitPlayer,
                        PlayersRegistry.getInstance().getPlayer(ieds.getIndirectSource().getUniqueId())
                ));
            } else {
                Sponge.getEventManager().post(new DummyKilledByEntityEvent(
                        kitPlayer,
                        ieds.getIndirectSource()
                ));
            }
            return;
        }

        if(damageSource instanceof EntityDamageSource){
            EntityDamageSource eds = (EntityDamageSource)damageSource;
            if(eds.getSource() instanceof Player){
                Sponge.getEventManager().post(new DummyKilledByPlayerEvent(
                        kitPlayer,
                        PlayersRegistry.getInstance().getPlayer(eds.getSource().getUniqueId())
                ));
            } else {
                Sponge.getEventManager().post(new DummyKilledByEntityEvent(
                        kitPlayer,
                        eds.getSource()
                ));
            }
            return;
        }

        if(!damageSource.getType().equals(DamageTypes.CUSTOM)){
            Sponge.getEventManager().post(new DummyKilledEvent(
                    kitPlayer
            ));
        }
    }

    @Listener
    public void onEntityDeath(DestructEntityEvent.Death event, @First DamageSource damageSource){
        event.setMessageCancelled(true);

        KitPlayer owner = PlayersRegistry.getInstance().getPlayer(event.getTargetEntity().getCreator().orElse(null));

        if(owner == null){
            return;
        }

        if(damageSource instanceof IndirectEntityDamageSource){
            IndirectEntityDamageSource ieds = (IndirectEntityDamageSource)damageSource;
            Sponge.getEventManager().post(
                    new EntityKilledEvent(owner, ieds.getIndirectSource())
            );
            return;
        }

        if(damageSource instanceof EntityDamageSource){
            EntityDamageSource eds = (EntityDamageSource)damageSource;
            Sponge.getEventManager().post(
                    new EntityKilledEvent(owner, eds.getSource())
            );
            return;
        }

        if(damageSource.getType().equals(DamageTypes.CUSTOM)){
            Sponge.getEventManager().post(
                    new EntityKilledEvent(owner, null)
            );
            return;
        }
    }
}
