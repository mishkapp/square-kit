package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.EntityUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mishkapp.minecraft.plugins.squarekit.utils.MathUtils.*;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 03.02.17.
 */
public class EnergyBeam extends UseSuffix {

    private boolean isCharging = false;
    private final double realCooldown;
    private final double realManacost;
    private double maxDistance = 50;
    private long maxCharge = 100;
    private double maxDamage = 100.0;
    private double maxManacost = 50;

    private List<ParticleEffect> chargeParticles = new ArrayList<>();
    private Vector3d lastCenter = new Vector3d();
    private Vector3d lastLookVec = new Vector3d();
    private Vector3d lastOPos = new Vector3d();
    private int lastCharge = 0;

    public EnergyBeam(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        realCooldown = cooldown;
        realManacost = manaCost;
        if(args.length > 2){
            maxDistance = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            maxCharge = Long.parseLong(args[3]);
        }
        if(args.length > 4){
            maxDamage = Double.parseDouble(args[4]);
        }
        if(args.length > 5){
            maxManacost = Double.parseDouble(args[5]);
        }
        prepareParticles();
    }

    private void prepareParticles(){
        for(int i = 0; i < maxCharge; i++){
            java.awt.Color hsbColor = java.awt.Color.getHSBColor((((i / (float) maxCharge) * (300.0f / 360.0f))), 1.0f, 1.0f);
            ParticleEffect pe = ParticleEffect.builder()
                    .type(ParticleTypes.REDSTONE_DUST)
                    .option(ParticleOptions.COLOR, Color.ofRgb(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue()))
                    .quantity(1)
                    .build();
            chargeParticles.add(pe);
        }
    }

    @Override
    protected boolean predicate() {
        if(isCharging){
            manaCost = 0;
        } else {
            manaCost = realManacost;
        }

        return true;
    }

    @Override
    protected void preUse(){
        if(isCharging){
            cooldown = realCooldown;
        } else {
            cooldown = 0;
        }
    }

    @Override
    protected void onUse() {
        if(isCharging){
            shot();
        } else {
            charge();
        }
    }

    private void charge(){
        isCharging = true;
        AtomicInteger step = new AtomicInteger(0);
        Sponge.getScheduler().createTaskBuilder()
                .intervalTicks(1)
                .execute(task -> {
                    if(!isCharging){
                        task.cancel();
                        return;
                    }
                    if(step.get() >= maxCharge){
                        isCharging = false;
                        cooldown = realCooldown;
                        shot();
                        task.cancel();
                        return;
                    }
                    double tickManaCost = maxManacost / maxCharge;
                    if(getPlayer().getCurrentMana() < tickManaCost){
                        task.cancel();
                        shot();
                        return;
                    }
                    getPlayer().setCurrentMana(getPlayer().getCurrentMana() - tickManaCost);

                    Player player = kitPlayer.getMcPlayer();
                    Vector3d oPos = player.getLocation().getPosition().add(0, 1.6, 0);
                    Vector3d lookVec = player.getHeadRotation();
                    Vector3d centralPos = oPos.add(0, 0, 1);
                    lastCenter = centralPos;
                    lastLookVec = lookVec;
                    lastOPos = oPos;
                    lastCharge = step.incrementAndGet();

                    chargeSound();
                    chargeParticles();
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    private void chargeSound(){
        if(lastCharge % 4 == 0){
            getPlayer().getMcPlayer().playSound(SoundTypes.BLOCK_NOTE_SNARE, lastOPos, 1, (lastCharge/4));
        }
    }

    private void chargeParticles(){
        List<Vector3d> points = new ArrayList<>();
        double r = ((maxCharge - lastCharge) / (double) maxCharge) + 0.25;
        double d = (lastCharge / (double) maxCharge) * (PI);
        for (double i = 0; i < PI * 2; i += PI/4){
            points.add(lastCenter.add(
                    r * sin(i + d),
                    r * cos(i + d),
                    0
            ));
        }

        points = rotatePoints(points, lastOPos, lookToRot(lastLookVec));

        ParticleEffect particle = chargeParticles.get(lastCharge - 1);
        points.forEach(p -> getPlayer().getMcPlayer().getWorld().spawnParticles(particle, p));
    }

    private void shot(){
        isCharging = false;
        if(lastCharge == 0){
            return;
        }
        Entity target = getTarget();

        double distance = getDistance();
        double damage = (lastCharge/ (double)maxCharge) * maxDamage;
        if(target != null){
            Vector3d headshotPoint = isHeadshot(target);
            boolean headshot = headshotPoint != null;
            if(headshot){
                damage *= 2;
                headshotSound();
                if(target instanceof Player){
                    KitPlayer targetPlayer = PlayersRegistry.getInstance().getPlayer(target.getUniqueId());
                    double targetHp = targetPlayer.getHealth();
                    if(damage > targetHp){
                        kitPlayer.addMana((lastCharge/maxCharge) * maxManacost);
                    }
                }
            }
            dealDamage(target, headshot);

            distance = headshotPoint.distance(getStartPoint());
        }

        shotSound();
        shotParticles(distance);
        lastCharge = 0;
    }

    private Entity getTarget(){
        return getPlayer().getMcPlayer()
                .getNearbyEntities(getDistance() + 1)
                .stream()
                .filter(entity -> {
                    if(!(entity instanceof Living)){
                        return false;
                    }

                    if(entity == getPlayer().getMcPlayer()){
                        return false;
                    }

                    Tuple<Vector3d, Vector3d> intersectionPoint = entity.getBoundingBox().get()
                            .intersects(getStartPoint(), getDirection()).orElse(null);
                    if(intersectionPoint == null){
                        return false;
                    }

                    return true;
                })
                .sorted(Comparator.comparingDouble(e -> e.getLocation().getPosition().distance(getPlayer().getMcPlayer().getLocation().getPosition())))
                .findFirst().orElse(null);
    }

    private Vector3d isHeadshot(Entity target){
        Vector3d result = null;
        AABB aabb = target.getBoundingBox().get();
        if(target instanceof Humanoid){
            AABB headAABB = new AABB(
                    aabb.getMin().getX(), aabb.getMin().getY() + 1.3, aabb.getMin().getZ(),
                    aabb.getMax().getX(), aabb.getMax().getY(), aabb.getMax().getZ()
            );
            Tuple<Vector3d, Vector3d> intersectionPoint = headAABB.intersects(getStartPoint(), getDirection()).orElse(null);
            if(intersectionPoint != null){
                result = intersectionPoint.getFirst();
            }
        }
        return result;
    }

    private void headshotSound(){
        Player player = getPlayer().getMcPlayer();
        player.playSound(SoundTypes.BLOCK_NOTE_HARP, player.getLocation().getPosition(), 1);
    }

    private double getDamage(){
        return (lastCharge/ maxCharge) * maxDamage;
    }

    private void dealDamage(Entity target, boolean headshot){
        double damage = (lastCharge/ maxCharge) * maxDamage;
        if(headshot){
            damage *= 2;
        }
        target.damage(damage, DamageUtils.pureDamage(getPlayer().getMcPlayer()));
    }

    private void shotSound(){
        Player player = getPlayer().getMcPlayer();
        player.playSound(SoundTypes.ENTITY_ELDER_GUARDIAN_CURSE, player.getLocation().getPosition(), 1);
    }

    private void shotParticles(double distance){
        for(double i = 0; i < distance; i += 0.1){
            final double r = i;
            Vector3d point = lastCenter.add(0, 0, r);
            Vector3d offPoint = point.add(0.5 * sin(r), 0.5 * cos(r), 0);

            point = rotatePoint(point, lastOPos, lookToRot(lastLookVec));
            offPoint = rotatePoint(offPoint, lastOPos, lookToRot(lastLookVec));


            getPlayer().getMcPlayer().getWorld().spawnParticles(
                    chargeParticles.get(lastCharge - 1),
                    point);

            getPlayer().getMcPlayer().getWorld().spawnParticles(
                    chargeParticles.get(lastCharge - 1),
                    offPoint);
        }
    }

    private Vector3d getStartPoint(){
        return rotatePoint(lastCenter, lastOPos, lookToRot(lastLookVec));
    }

    private Vector3d getEndPoint(){
        return EntityUtils.getBlockRayHitPoint(getPlayer().getMcPlayer(), maxDistance);
    }

    private Vector3d getDirection(){
        return getEndPoint().sub(getStartPoint()).normalize();
    }

    private double getDistance(){
        return getStartPoint().distance(getEndPoint());
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.energy-beam")
                .replace("%MAX_DAMAGE%", FormatUtils.unsignedRound(maxDamage))
                .replace("%MAX_DISTANCE%", FormatUtils.unsignedRound(maxDistance))
                .replace("%CHARGE_MANACOST%", FormatUtils.unsignedTenth(maxManacost/(maxCharge /20.0)))
                .replace("%CHARGE_TIME%", FormatUtils.unsignedHundredth(maxCharge /20.0))
                + super.getLoreEntry();
    }
}
