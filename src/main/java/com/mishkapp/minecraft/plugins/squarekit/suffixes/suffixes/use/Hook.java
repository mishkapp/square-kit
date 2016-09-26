package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Use;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by mishkapp on 15.07.2016.
 */
public class Hook extends Use {

    private double manaCost;

    public Hook(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        cooldown = 45 * 1000;
        manaCost = (50.0 - (50.0/4096.0) * level) / 100;
    }

    @Override
    protected boolean isItemPresent(Player player) {
        return false;
    }

    @Override
    public void register(KitPlayer player) {

    }

    @Override
    public void handle(KitEvent event, KitPlayer kitPlayer) {
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemPresentInHand(player)){
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
