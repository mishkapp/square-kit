package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.use.UseSuffix;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Silverfish;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.magicDamage;
import static java.lang.Math.sin;

/**
 * Created by mishkapp on 14.12.2016.
 */
public class LivingMine extends UseSuffix {


    private double damage = 15;
    private int liveTime = 30 * 20;
    private int time = 10;
    private double speedReduction = 0.2;
    private int activationTime = 3;

    private ParticleEffect smoke = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.LARGE_SMOKE)
            .build();

    private PotionEffect blindness = PotionEffect.builder()
            .potionType(PotionEffectTypes.BLINDNESS)
            .duration(time * 20)
            .amplifier(1)
            .build();

    private List<Silverfish> mines = new ArrayList<>();

    private Random random = new Random();

    public LivingMine(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 10 * 1000;
        manaCost = 15;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof EntityCollideEntityEvent){
            EntityCollideEntityEvent entityCollideEntityEvent = (EntityCollideEntityEvent)event;
            Entity playersEntity = entityCollideEntityEvent.getPlayersEntity();
            if(!mines.contains(playersEntity)){
                return;
            }
            onCollide(entityCollideEntityEvent.getAffectedEntity());
            playersEntity.remove();
            mines.remove(playersEntity);
        }
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(AreaRegistry.getInstance().isInSafeArea(player)){
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

            World world = player.getWorld();

            Vector3d spawnLoc = player.getLocation().getPosition();

            final Silverfish mine = (Silverfish) player.getWorld().createEntity(EntityTypes.SILVERFISH, spawnLoc);

            mine.setCreator(player.getUniqueId());
            mine.offer(Keys.WALKING_SPEED, 0.2);

            mine.offer(Keys.AI_ENABLED, false);

            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(activationTime * 20)
                    .execute(t -> mine.offer(Keys.AI_ENABLED, true))
                    .submit(SquareKit.getInstance().getPlugin());

            mines.add(mine);

            world.spawnEntity(mine,
                    Cause.builder()
                            .owner(SquareKit.getInstance())
                            .build());

            SpongeUtils.getTaskBuilder()
                    .delayTicks(liveTime)
                    .execute(o -> {
                        if(mine.isRemoved()){
                            return;
                        }
                        mine.remove();
                    })
                    .submit(SquareKit.getInstance());
        }
    }

    private void onCollide(Entity affected){
        List<PotionEffect> effects = affected.get(Keys.POTION_EFFECTS).orElse(new ArrayList<>());
        effects.add(blindness);
        affected.offer(Keys.POTION_EFFECTS, effects);
        affected.damage(damage, magicDamage(kitPlayer.getMcPlayer()));
        affected.getWorld().playSound(SoundTypes.BLOCK_CLOTH_BREAK, affected.getLocation().getPosition(), 2);

        for(int i = 0; i < 10; i++){
            affected.getWorld().spawnParticles(
                    smoke,
                    affected.getLocation().getPosition()
                            .add(
                                    3 * sin(random.nextGaussian()),
                                    3 * sin(random.nextGaussian()),
                                    3 * sin(random.nextGaussian())
                            )
            );
        }

        if(affected instanceof Player){
            KitPlayer affectedPlayer = PlayersRegistry.getInstance().getPlayer(affected.getUniqueId());
            affectedPlayer.getSpeedAdds().put(this, -1 * speedReduction);
            affectedPlayer.updateStats();
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(time * 20)
                    .execute(r -> {
                        affectedPlayer.getSpeedAdds().remove(this);
                        affectedPlayer.updateStats();
                    })
                    .submit(SquareKit.getInstance().getPlugin());
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("living-mine-suffix")
                .replace("%DAMAGE%", FormatUtils.unsignedRound(damage))
                .replace("%SPEEDREDUCTION%", FormatUtils.unsignedRound(speedReduction * 100))
                .replace("%ACTIVATIONTIME%", FormatUtils.unsignedRound(activationTime))
                .replace("%TIME%", FormatUtils.unsignedTenth(time))
                .replace("%LIVETIME%", FormatUtils.unsignedRound(damage))
                + super.getLoreEntry();
    }
}
