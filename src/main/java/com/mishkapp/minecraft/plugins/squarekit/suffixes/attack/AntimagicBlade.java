package com.mishkapp.minecraft.plugins.squarekit.suffixes.attack;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.PlayerAttackEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.util.Random;

import static org.spongepowered.api.event.cause.entity.damage.DamageTypes.MAGIC;

/**
 * Created by mishkapp on 13.12.2016.
 */
public class AntimagicBlade extends Suffix {
    private ParticleEffect effect;
    private Random rnd = new Random();
    private double damage = 0.2;

    public AntimagicBlade(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        effect = ParticleEffect.builder()
                .type(ParticleTypes.WATER_SPLASH)
                .quantity(3)
                .offset(new Vector3d(0, 1, 0))
                .build();
        damage = level;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof PlayerAttackEntityEvent){
            PlayerAttackEntityEvent attackEvent = (PlayerAttackEntityEvent)event;
            if(!isWeaponInHand()){
                return;
            }
            if(!(attackEvent.getAttacked() instanceof Player)){
                return;
            }
            KitPlayer attacked = PlayersRegistry.getInstance().getPlayer(attackEvent.getAttacked().getUniqueId());

            attacked.getMcPlayer().damage(
                    attacked.getCurrentMana() * damage,
                    EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).magical().bypassesArmor().type(MAGIC).build());
            addEffect(attacked.getMcPlayer());
        }
    }

    private void addEffect(Entity entity){
        World world = entity.getWorld();
        for(int i = 0; i < 10; i++){
            world.spawnParticles(effect,
                    entity.getLocation().getPosition().add(
                            rnd.nextGaussian() / 2,
                            rnd.nextGaussian() / 2,
                            rnd.nextGaussian() / 2
                    ));
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("antimagic-blade-suffix")
                .replace("%DAMAGE%", FormatUtils.round(damage));
    }
}
