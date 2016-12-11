package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityKilledEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Random;

import static java.lang.Math.sin;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class Dummy extends UseSuffix {
    private Human lastDummy = null;
    private int duration = 30;
    private int radius = 10;
    private int damage = 30;

    private ParticleEffect smoke = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.FIRE_SMOKE)
            .build();

    private Random random = new Random();

    public Dummy(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 3.5 * 1000;
        manaCost = 10 - (level * 64.0/10);
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof EntityKilledEvent){
            if(lastDummy != null){
                onDummyKilled(((EntityKilledEvent) event).getKiller());
            }
        }
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
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

            if(lastDummy != null){
                lastDummy.remove();
                lastDummy = null;
            }

            createDummy();
        }
    }


    public void createDummy(){
        Player player = kitPlayer.getMcPlayer();
        World world = player.getWorld();
        Human dummy = (Human) world.createEntity(EntityTypes.HUMAN, player.getLocation().getPosition());
        dummy.setHelmet(player.getHelmet().orElse(null));
        player.setHelmet(null);
        dummy.setChestplate(player.getChestplate().orElse(null));
        player.setChestplate(null);
        dummy.setLeggings(player.getLeggings().orElse(null));
        player.setLeggings(null);
        dummy.setBoots(player.getBoots().orElse(null));
        player.setBoots(null);
        dummy.setCreator(player.getUniqueId());
        dummy.offer(Keys.DISPLAY_NAME, Text.of(player.getName()));
        dummy.offer(Keys.CUSTOM_NAME_VISIBLE, true);

        lastDummy = dummy;

        world.spawnEntity(
                dummy,
                Cause.builder()
                        .owner(SquareKit.getInstance())
                        .build()
        );

        Sponge.getScheduler().createTaskBuilder()
                .delayTicks(duration * 20)
                .execute(r -> {
                    if(dummy.isRemoved()){
                        return;
                    }
                    dummy.remove();
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    public void onDummyKilled(Entity killer){
        World world = lastDummy.getWorld();
        world.playSound(SoundTypes.ENTITY_GENERIC_EXPLODE, lastDummy.getLocation().getPosition(), 4);
        Collection<Entity> entities = lastDummy.getNearbyEntities(radius);
        entities.forEach(e -> {
            DamageSource source = EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).type(DamageTypes.PROJECTILE).bypassesArmor().build();
            e.damage(damage, source);
        });

        for(int i = 0; i < 80; i++){
            world.spawnParticles(
                    smoke,
                    lastDummy.getLocation().getPosition()
                    .add(
                            radius * sin(random.nextGaussian()),
                            radius * sin(random.nextGaussian()),
                            radius * sin(random.nextGaussian())
                    )
            );
        }
        lastDummy = null;
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("dummy-suffix")
                .replace("%DURATION%", duration + "")
                .replace("%DAMAGE%", damage + "")
                .replace("%RADIUS%", radius + "")
                + super.getLoreEntry();
    }
}
