package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Use;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static java.lang.Math.*;

/**
 * Created by mishkapp on 15.07.2016.
 */
public class Hook extends Use {

    private double manaCost;

    public Hook(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);
        cooldown = 45 * 1000;
        manaCost = (50.0 - (50.0/4096.0) * level) / 100;
    }

    @Override
    protected boolean isItemPresent() {
        return false;
    }

    @Override
    public void register() {

    }

    @Override
    public void handle(KitEvent event) {
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemPresentInHand()){
                return;
            }
            //TODO: MANA
//            float currentMana = player.getExp();
//            if(currentMana < manaCost){
//                kitPlayer.getMcPlayer().sendMessage(Messages.getMessage("nomana"));
//                return;
//            }
//            if(!isCooldowned(kitPlayer)){
//                return;
//            }

//            lastUse = System.currentTimeMillis();

            World world = player.getWorld();

            Vector3d spawnPos = player.getLocation().getPosition();
            Vector3d lookVector = player.getHeadRotation();

            spawnPos = spawnPos.add(
                    -1 * sin(toRadians(lookVector.getY())),
                    1.75,
                    cos(toRadians(lookVector.getY()))
            );

            Snowball hook = player.launchProjectile(Snowball.class).orElse(null);

//            hook.setVelocity(new Vector3d(
//                    -1 * sin(toRadians(lookVector.getY())),
//                    -1 * sin(toRadians(lookVector.getX())),
//                    cos(toRadians(lookVector.getY()))
//            ));


            addEffect(kitPlayer.getMcPlayer());
        }
    }

    private void addEffect(Player player){
//        EffectManager effectManager = SquareKit.getInstance().getEffectManager();
//        ShieldEffect effect = new ShieldEffect(effectManager);
//        effect.setEntity(player);
//        effect.iterations = 5 * 20;
//        effect.particle = ParticleEffect.SLIME;
//        effect.radius = 2;
//        effect.particles = 25;
//        effect.start();
    }

    @Override
    public String getLoreEntry() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return "Скорлупа: При использовании повышает \nPR на 80% и понижает ATK до 3 на 5с. §9" + formatter.format(manaCost)
                + "MP §7" + formatter.format(cooldown/1000.0) + "c." ;
    }
}
