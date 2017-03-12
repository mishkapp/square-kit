package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Random;

/**
 * Created by mishkapp on 13.12.2016.
 */
public class MagicLeech extends TargetedProjectileSuffix{

    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.DRIP_WATER)
            .build();

    private Random random = new Random();

    private int time = 18;

    public MagicLeech(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);

        cooldown = 15;
        manaCost = 0;
        hSpeed = 0.75;
        vSpeed = 0.75;
        liveTime = 3 * 20;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
    }

    @Override
    protected Entity prepareEntity() {
        return kitPlayer.getMcPlayer().getWorld().createEntity(EntityTypes.ENDERMITE, kitPlayer.getMcPlayer().getLocation().getPosition().add(0, 1.75, 0));
    }

    @Override
    protected void onLaunch(Entity projectile, Entity target) {}

    @Override
    protected void onCollide(Entity affected){
        addCollideEffect(affected);

        if(!(affected instanceof Player)){
            return;
        }
        KitPlayer affectedPlayer = PlayersRegistry.getInstance().getPlayer(affected.getUniqueId());

        Double manaRegen = affectedPlayer.getManaRegen();

        if(manaRegen > 0){
            manaRegen *= -1;
            affectedPlayer.getManaRegenAdds().put(this, manaRegen * 2);
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(time * 20)
                    .execute(r -> affectedPlayer.getManaRegenAdds().remove(this))
                    .submit(SquareKit.getInstance().getPlugin());
        }

        if(manaRegen <= 0){
            affectedPlayer.getHealthRegenAdds().put(this, manaRegen * 1.2);
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(time * 20)
                    .execute(r -> affectedPlayer.getHealthRegenAdds().remove(this))
                    .submit(SquareKit.getInstance().getPlugin());
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
        return Messages.get("suffix.magic-leech")
                .replace("%TIME%", FormatUtils.unsignedTenth(time))
                + super.getLoreEntry();
    }
}
