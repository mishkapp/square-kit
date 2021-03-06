package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.PlayerUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mishkapp.minecraft.plugins.squarekit.utils.MathUtils.lookToRot;
import static com.mishkapp.minecraft.plugins.squarekit.utils.MathUtils.rotatePointsInv;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

/**
 * Created by mishkapp on 03.03.17.
 */
public class EnergyShield extends UseSuffix {

    private boolean isCharging = false;
    private final double realCooldown;
    private final double realManacost;
    private long chargeTicks = 100;
    private double maxDistance = 15.0;
    private double maxBlindDuration = 15.0;
    private double maxMRes = 0.5;
    private double maxPRes = 0.5;
    private double maxManacost = 50;

    private List<ParticleEffect> chargeParticles = new ArrayList<>();
    private Vector3d lastCenter = new Vector3d();
    private Vector3d lastLookVec = new Vector3d();
    private Vector3d lastOPos = new Vector3d();
    private int lastCharge = 0;

    public EnergyShield(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        realCooldown = cooldown;
        realManacost = manaCost;
        if(args.length > 2){
            maxDistance = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            chargeTicks = Long.parseLong(args[3]);
        }
        if(args.length > 4){
            maxBlindDuration = Double.parseDouble(args[4]);
        }
        if(args.length > 5){
            maxPRes = Double.parseDouble(args[5]);
        }
        if(args.length > 6){
            maxMRes = Double.parseDouble(args[6]);
        }
        if(args.length > 7){
            maxManacost = Double.parseDouble(args[7]);
        }
        prepareParticles();
    }

    private void prepareParticles(){
        for(int i = 0; i < chargeTicks; i++){
            java.awt.Color hsbColor = java.awt.Color.getHSBColor((((i / (float)chargeTicks) * (300.0f / 360.0f))), 1.0f, 1.0f);
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
            explode();
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
                    if(step.get() >= chargeTicks){
                        isCharging = false;
                        cooldown = realCooldown;
                        explode();
                        task.cancel();
                        return;
                    }

                    double tickManaCost = maxManacost / chargeTicks;
                    if(getPlayer().getCurrentMana() < tickManaCost){
                        task.cancel();
                        explode();
                        return;
                    }
                    getPlayer().setCurrentMana(getPlayer().getCurrentMana() - tickManaCost);

                    Player player = kitPlayer.getMcPlayer();
                    Vector3d oPos = player.getLocation().getPosition().add(0, 1, 0);
                    Vector3d lookVec = player.getHeadRotation();
                    lastCenter = oPos;
                    lastLookVec = lookVec;
                    lastOPos = oPos;
                    lastCharge = step.incrementAndGet();

                    chargeSound();
                    chargeParticles();
                    updateStats();
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
        double r = 1;
        double d = (lastCharge / (double)chargeTicks) * (PI);

        for(int i = 0; i < 3; i++){

            List<Vector3d> tempPoints = new ArrayList<>();
            int particlesCount = lastCharge / 10;
            for(int j = 0; j < particlesCount; j++){
                double offset = (lastCharge / (double)chargeTicks) * (PI * 2);
                switch (i){
                    case 1: {
                        offset -= d;
                        break;
                    }
                    default: {
                        offset += d;
                    }
                }
                double pos = j * ((PI * 2) / particlesCount);
                tempPoints.add(lastCenter.add(
                        r * sin(pos + offset),
                        0,
                        r * cos(pos + offset)
                ));
            }

            double yRot = lookToRot(lastLookVec).getY();

            switch (i){
                case 1: {
                    tempPoints = rotatePointsInv(tempPoints, lastOPos, new Vector3d(0, yRot, 45));
                    break;
                }
                case 2: {
                    tempPoints = rotatePointsInv(tempPoints, lastOPos, new Vector3d(0, yRot, -45));
                    break;
                }
            }
            tempPoints.forEach(points::add);
        }

        ParticleEffect particle = chargeParticles.get(lastCharge - 1);
        points.forEach(p -> getPlayer().getMcPlayer().getWorld().spawnParticles(particle, p));
    }

    private void explode(){
        isCharging = false;
        if(lastCharge == 0){
            return;
        }

        List<Entity> entities = getAffectedEntities();

        explodeSound();
        explodeParticles(entities);
        explodeEffect(entities);
        updateStats();

        lastCharge = 0;
    }

    private void explodeSound(){
        Player player = getPlayer().getMcPlayer();
        player.playSound(SoundTypes.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, player.getLocation().getPosition(), 1.5);
    }

    private List<Entity> getAffectedEntities(){
        double distance = (lastCharge/(double)chargeTicks) * maxDistance;
        return getPlayer().getMcPlayer()
                .getNearbyEntities(distance)
                .stream()
                .filter(entity -> {
                    if(!(entity instanceof Living)){
                        return false;
                    }

                    if(entity == getPlayer().getMcPlayer()){
                        return false;
                    }

                    Vector3d entityPos = entity.getLocation().getPosition();
                    Vector3d direction = entityPos.sub(lastCenter).normalize();
                    Tuple<Vector3d, Vector3d> intersectionPoint = entity.getBoundingBox().get()
                            .intersects(lastCenter, direction).orElse(null);
                    if(intersectionPoint == null){
                        return false;
                    }
                    if(intersectionPoint.getFirst().distance(entityPos) > 0.5){
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private void explodeParticles(List<Entity> entities){
        Random random = new Random();
        Player player = getPlayer().getMcPlayer();
        ParticleEffect particle = chargeParticles.get(lastCharge - 1);
        entities.forEach(entity -> {
            for(int i = 0; i < 16; i++){
                player.getWorld().spawnParticles(
                        particle,
                        entity.getLocation().getPosition().add(random.nextGaussian()/2, 1.6 + random.nextGaussian()/2, random.nextGaussian()/2)
                );
            }
        });

        for(int i = 0; i < 64; i++){
            player.getWorld().spawnParticles(
                    particle,
                    lastCenter.add(random.nextGaussian() * 2, 2 + random.nextGaussian(), random.nextGaussian() * 2));
        }
    }

    private void explodeEffect(List<Entity> entities){
        int blindDuration = (int)(((lastCharge/(double)chargeTicks) * maxBlindDuration) * 20);
        PotionEffect pe = PotionEffect.builder()
                .potionType(PotionEffectTypes.BLINDNESS)
                .duration(blindDuration)
                .amplifier(1)
                .build();
        entities.forEach(e -> PlayerUtils.applyEffects(e, pe));
    }

    private void updateStats(){
        kitPlayer.getMagicResistAdds().put(this, (lastCharge/(double)chargeTicks) * maxMRes);
        kitPlayer.getPhysicalResistAdds().put(this, (lastCharge/(double)chargeTicks) * maxPRes);
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.energy-shield")
                .replace("%MAX_DISTANCE%", FormatUtils.unsignedRound(maxDistance))
                .replace("%MAX_BLIND_DURATION%", FormatUtils.unsignedRound(maxBlindDuration))
                .replace("%MAX_PRES%", FormatUtils.unsignedRound(maxPRes * 100))
                .replace("%MAX_MRES%", FormatUtils.unsignedRound(maxMRes * 100))
                .replace("%CHARGE_MANACOST%", FormatUtils.unsignedTenth(maxManacost/(chargeTicks/20.0)))
                .replace("%CHARGE_TIME%", FormatUtils.unsignedHundredth(chargeTicks/20.0))
                + super.getLoreEntry();
    }
}
