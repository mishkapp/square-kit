package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
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
public class MagicShot extends UseSuffix {

    private boolean isCharging = false;
    private final double realCooldown;
    private final double realManacost;
    private double maxDistance = 50;
    private long chargeTicks = 100;
    private double maxDamage = 100.0;
    private double maxManacost = 50;

    private List<ParticleEffect> chargeParticles = new ArrayList<>();
    private Vector3d lastCenter = new Vector3d();
    private Vector3d lastLookVec = new Vector3d();
    private Vector3d lastOPos = new Vector3d();
    private int lastCharge = 0;

    public MagicShot(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        realCooldown = cooldown;
        realManacost = manaCost;
        prepareParticles();
        if(args.length > 2){
            maxDistance = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            chargeTicks = Long.parseLong(args[3]);
        }
        if(args.length > 4){
            maxDamage = Double.parseDouble(args[4]);
        }
        if(args.length > 5){
            maxManacost = Double.parseDouble(args[5]);
        }
    }

    private void prepareParticles(){
        for(int i = 0; i < 64; i++){
            java.awt.Color hsbColor = java.awt.Color.getHSBColor((((i / 64.0f) * (300.0f / 360.0f))), 1.0f, 1.0f);
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
            cooldown = realCooldown;
        } else {
            cooldown = 0;
            manaCost = realManacost;
        }

        return true;
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
        cooldown = 0;
        manaCost = 0;
        isCharging = true;
        lastUse = (long) (System.currentTimeMillis() + (cooldown * 1000));
        AtomicInteger step = new AtomicInteger(0);
        Sponge.getScheduler().createTaskBuilder()
                .intervalTicks(1)
                .execute(task -> {
                    if(!isCharging){
                        task.cancel();
                        return;
                    }
                    if(step.get() >= chargeTicks){
                        isCharging = false;
                        shot();
                        task.cancel();
                        return;
                    }
                    step.incrementAndGet();

                    double tickManaCost = maxManacost / chargeTicks;
                    if(getPlayer().getCurrentMana() < tickManaCost){
                        task.cancel();
                        shot();
                        return;
                    }
                    getPlayer().setCurrentMana(getPlayer().getCurrentMana() - tickManaCost);

                    List<Vector3d> points = new ArrayList<>();
                    Player player = kitPlayer.getMcPlayer();
                    Vector3d oPos = player.getLocation().getPosition().add(0, 1.6, 0);
                    Vector3d lookVec = player.getHeadRotation();
                    Vector3d centralPos = oPos.add(0, 0, 1);
                    lastCenter = centralPos;
                    lastLookVec = lookVec;
                    lastOPos = oPos;
                    lastCharge = step.get();

                    if(step.get() % 4 == 0){
                        player.playSound(SoundTypes.BLOCK_NOTE_SNARE, oPos, 1, (step.get()/4));
                    }

                    double r = ((chargeTicks - step.get()) / (double)chargeTicks);
                    double d = (step.get() / (double)chargeTicks) * (PI);
                    for (double i = 0; i < PI * 2; i += PI/4){
                        points.add(centralPos.add(
                                r * sin(i + d),
                                r * cos(i + d),
                                0
                        ));
                    }

                    points = rotatePoints(points, oPos, lookToRot(lookVec));

                    ParticleEffect particle = chargeParticles.get((int) ((step.get()/(double)chargeTicks) * 63));
                    points.forEach(p -> player.getWorld().spawnParticles(particle, p));
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    private void shot(){
        isCharging = false;
        lastUse = System.currentTimeMillis();
        final Vector3d startPoint = rotatePoint(lastCenter, lastOPos, lookToRot(lastLookVec));

        Player player = getPlayer().getMcPlayer();

        Vector3d endPoint = EntityUtils.getBlockRayHitPoint(player, maxDistance);

        Vector3d direction = endPoint.sub(startPoint).normalize();

        double distance = startPoint.distance(endPoint);

        Entity target = getPlayer().getMcPlayer()
                .getNearbyEntities(distance + 1)
                .stream()
                .filter(entity -> {
                    if(!(entity instanceof Living)){
                        return false;
                    }

                    if(entity == getPlayer().getMcPlayer()){
                        return false;
                    }

                    Tuple<Vector3d, Vector3d> intersectionPoint = entity.getBoundingBox().get()
                            .intersects(startPoint, direction).orElse(null);
                    if(intersectionPoint == null){
                        return false;
                    }

                    return true;
                })
                .sorted(Comparator.comparingDouble(e -> e.getLocation().getPosition().distance(player.getLocation().getPosition())))
                .findFirst().orElse(null);

        if(target != null){
            boolean headshot = false;
            AABB aabb = target.getBoundingBox().get();
            if(target instanceof Humanoid){
                AABB headAABB = new AABB(
                        aabb.getMin().getX(), aabb.getMin().getY() + 1.3, aabb.getMin().getZ(),
                        aabb.getMax().getX(), aabb.getMax().getY(), aabb.getMax().getZ()
                        );
                if(headAABB.intersects(startPoint, direction).orElse(null) != null){
                    headshot = true;
                }
            }

            if(headshot){
                player.playSound(SoundTypes.BLOCK_NOTE_HARP, player.getLocation().getPosition(), 1);
            }
            double damage = (lastCharge/chargeTicks) * maxDamage;
            target.damage(damage, DamageUtils.pureDamage(player));
            distance = aabb.intersects(startPoint, direction).get().getFirst().distance(startPoint);
        }

        player.playSound(SoundTypes.ENTITY_FIREWORK_LAUNCH, player.getLocation().getPosition(), 1);

        for(double i = 0; i < distance; i += 0.1){
            final double r = i;
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks((long) i / 10)
                    .execute(t -> {
                        Vector3d point = lastCenter.add(0, 0, r);
                        Vector3d offPoint = point.add(0.5 * sin(r), 0.5 * cos(r), 0);

                        point = rotatePoint(point, lastOPos, lookToRot(lastLookVec));
                        offPoint = rotatePoint(offPoint, lastOPos, lookToRot(lastLookVec));


                        getPlayer().getMcPlayer().getWorld().spawnParticles(
                                chargeParticles.get((int) ((lastCharge /(double)chargeTicks) * 63)),
                                point);

                        getPlayer().getMcPlayer().getWorld().spawnParticles(
                                chargeParticles.get((int) ((lastCharge /(double)chargeTicks) * 63)),
                                offPoint);
                    })
                    .submit(SquareKit.getInstance().getPlugin());
        }

    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.magic-shot")
                .replace("%MAX_DAMAGE%", FormatUtils.unsignedRound(maxDamage))
                .replace("%MAX_DISTANCE%", FormatUtils.unsignedRound(maxDistance))
                .replace("%CHARGE_MANACOST%", FormatUtils.unsignedTenth(maxManacost/(chargeTicks/20.0)))
                .replace("%CHARGE_TIME%", FormatUtils.unsignedHundredth(chargeTicks/20.0))
                + super.getLoreEntry();
    }
}
