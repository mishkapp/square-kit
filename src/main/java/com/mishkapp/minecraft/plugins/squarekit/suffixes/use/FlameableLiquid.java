package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.effects.Effect;
import com.mishkapp.minecraft.plugins.squarekit.effects.Flame;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class FlameableLiquid extends TargetedProjectileSuffix {
    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.FLAME)
            .build();

    private Entity lastEntity = null;

    private Vector3d lastVelocity;

    private Random random = new Random();

    private double damage = 10.0;
    private double time = 10;

    public FlameableLiquid(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 2){
            damage = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            time = Double.parseDouble(args[3]);
        }

        hSpeed = 0.5;
        vSpeed = 0.5;
//        correctionFactor = 0.15;
        liveTime = 7 * 20;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof EntityCollideEntityEvent){
            EntityCollideEntityEvent entityCollideEntityEvent = (EntityCollideEntityEvent)event;
            Entity playersEntity = entityCollideEntityEvent.getPlayersEntity();
            if(playersEntity != lastEntity){
                return;
            }
            onCollide(entityCollideEntityEvent.getAffectedEntity());
            lastEntity.remove();
        }
    }

    @Override
    protected Entity prepareEntity() {
        return kitPlayer.getMcPlayer().getWorld().createEntity(EntityTypes.FIREBALL, kitPlayer.getMcPlayer().getLocation().getPosition().add(0, 1.75, 0));
    }

    @Override
    protected void onLaunch(Entity projectile, Entity target) {}

    @Override
    protected void onCollide(Entity affected){
        lastVelocity = null;
        addCollideEffect(affected);
        DamageSource source = EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).type(DamageTypes.PROJECTILE).magical().bypassesArmor().build();
        affected.damage(damage, source);

        if(!(affected instanceof Player)){
            return;
        }
        KitPlayer affectedPlayer = PlayersRegistry.getInstance().getPlayer(affected.getUniqueId());
        List<Effect> effects = affectedPlayer.getEffects();

        List<Effect> flames = effects.parallelStream().filter(e -> e instanceof Flame && e.isRunning()).collect(Collectors.toList());

        if(flames.size() > 0){
            Flame flame = (Flame) flames.get(0);
            flame.setRunning(false);
            affectedPlayer.addEffect(new Flame(affectedPlayer, this, Math.min(flame.getLevel() + 1, 5), (long) (time * 1000)));
        } else {
            affectedPlayer.addEffect(new Flame(affectedPlayer, this, 1, (long) (time * 1000)));
        }
    }

    @Override
    protected void addTrailEffect(Entity entity){
        if(trailEffect == null || entity == null || entity.isRemoved() || entity.isOnGround()){
            return;
        }
        entity.getWorld().spawnParticles(
                trailEffect,
                entity.getLocation().getPosition().add(random.nextGaussian()/4, random.nextGaussian()/4, random.nextGaussian()/4)
        );
    }

    private void addCollideEffect(Entity entity){
        if(trailEffect == null || entity == null || entity.isRemoved() || entity.isOnGround()){
            return;
        }
        for(int i = 0; i < 25; i++)
        entity.getWorld().spawnParticles(
                trailEffect,
                entity.getLocation().getPosition().add(random.nextGaussian() * 1.2, random.nextGaussian() * 1.2, random.nextGaussian() * 1.2)
        );
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.flameable-liquid")
                .replace("%DAMAGE%", FormatUtils.unsignedRound(damage))
                .replace("%TIME%", FormatUtils.unsignedTenth(time))
                + super.getLoreEntry();
    }
}
