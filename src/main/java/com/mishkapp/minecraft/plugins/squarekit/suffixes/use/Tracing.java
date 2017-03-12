package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityCollideEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.SpongeUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.pureDamage;
import static com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils.applyEffects;
import static java.lang.Math.*;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 03.02.17.
 */
public class Tracing extends TargetedSuffix {
    private boolean isTracing = false;
    private List<Vector3d> points = new ArrayList<>();
    private Random random = new Random();
    private ParticleEffect particle = ParticleEffect.builder()
            .type(ParticleTypes.CLOUD)
            .quantity(1)
            .build();

    public Tracing(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
    }

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof EntityCollideEntityEvent){
            if(isTracing){
                EntityCollideEntityEvent eceEvent = (EntityCollideEntityEvent) event;
                if(eceEvent.getPlayersEntity().equals(kitPlayer.getMcPlayer())){
                    onCollide(eceEvent.getPlayersEntity(), eceEvent.getAffectedEntity());
                }
            }
        }
    }

    @Override
    protected boolean isPredicate(Entity target) {
        if(isTracing){
            return false;
        }

        if(kitPlayer.getMcPlayer().isOnGround()){
            return false;
        }

        double y0 = kitPlayer.getMcPlayer().getLocation().getY();

        double y = target.getBoundingBox().get().getCenter().getY();

        if(y0 - y < 15){
            return false;
        }

        return true;
    }

    @Override
    protected void onUse(final Entity target) {
        isTracing = true;
        Player player = kitPlayer.getMcPlayer();
        points.add(player.getLocation().getPosition());
        Vector3d lookVec = player.getHeadRotation();
        Vector3d thrustVec = new Vector3d(1, 1, 1);

        thrustVec = thrustVec.mul(
                -1 * sin(toRadians(lookVec.getY())),
                tan(toRadians(-1 * lookVec.getX())),
                cos(toRadians(lookVec.getY()))
        );


        //or maybe add??
        player.setVelocity(player.getVelocity().add(thrustVec));

        player.setCreator(player.getUniqueId());


        SpongeUtils.getTaskBuilder()
                .intervalTicks(1)
                .execute(task -> {
                    if(target.isRemoved()){
                        isTracing = false;
                    }
                    if(kitPlayer.getMcPlayer().isOnGround()){
                        isTracing = false;
                        receiveDamage();
                    }
                    if(kitPlayer.getMcPlayer().get(Keys.IS_SNEAKING).get()){
                        isTracing = false;
                    }
                    if(isTracing){
                        addPoint();
                        addSoundEffect(player, target);
                        addTrailEffect(player);
                        correctThrust(player, target);
                    } else {
                        points = new ArrayList<>();
                        task.cancel();
                    }
                })
                .submit(SquareKit.getInstance());
    }

    private void addPoint(){
        points.add(kitPlayer.getMcPlayer().getLocation().getPosition());
    }

    private void receiveDamage(){
        double damage = getTravelDistance();
        kitPlayer.getMcPlayer().damage(damage / 2, pureDamage(kitPlayer.getMcPlayer()));

    }

    private void addSoundEffect(Player player, Entity entity){
        double pitch = min(((getTravelDistance() / distance) * 2), 2);
        player.playSound(SoundTypes.BLOCK_NOTE_HARP, player.getLocation().getPosition(), 1, pitch);
        if(entity instanceof Player){
            ((Player) entity).playSound(SoundTypes.BLOCK_NOTE_HARP, entity.getLocation().getPosition(), 1, pitch);
        }
    }

    private void addTrailEffect(Player player) {
        for(int i = 0; i < 5; i++)
            player.getWorld().spawnParticles(
                    particle,
                    player.getLocation().getPosition().add(random.nextGaussian() * 0.5, random.nextGaussian() * 0.5, random.nextGaussian() * 0.5)
            );
    }

    private void correctThrust(Player player, Entity target) {
        double x0 = player.getLocation().getX();
        double y0 = player.getLocation().getY();
        double z0 = player.getLocation().getZ();

        double x = target.getBoundingBox().get().getCenter().getX() - x0;
        double y = target.getBoundingBox().get().getCenter().getY() - y0;
        double z = target.getBoundingBox().get().getCenter().getZ() - z0;

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

        Vector3d thrustVec = new Vector3d(1, 1, 1).mul(
                2 * -1 * sin(toRadians(phi)),
                2 * tan(toRadians(-1 * theta)),
                2 * cos(toRadians(phi))
        );

        thrustVec = thrustVec.add(0, -0.15, 0);

        player.setVelocity(thrustVec);
    }


    private void onCollide(Entity player, Entity affected) {
        isTracing = false;
        double damage = getTravelDistance();
        points = new ArrayList<>();

        affected.damage(damage, DamageUtils.pureDamageKnock(player));

        PotionEffect pe = PotionEffect.builder()
                .amplifier(1)
                .duration((int) ((damage / 3.33) * 20))
                .potionType(PotionEffectTypes.BLINDNESS)
                .build();

        applyEffects(affected, pe);
    }

    private double getTravelDistance(){
        double result = 0;
        for(int i = 1; i < points.size(); i++){
            result += points.get(i - 1).distance(points.get(i));
        }
        return result;
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.tracing")
                + super.getLoreEntry();
    }
}
