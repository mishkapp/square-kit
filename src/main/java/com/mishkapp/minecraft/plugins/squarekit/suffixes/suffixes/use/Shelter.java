package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.*;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Use;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by mishkapp on 30.06.2016.
 */
public class Shelter extends Use {

    private double manaCost;

    public Shelter(ItemStack itemStack, Integer level) {
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

            double currentMana = kitPlayer.getCurrentMana();
            if(currentMana < manaCost){
                return;
            }
            kitPlayer.setCurrentMana(currentMana - manaCost);

            HashMap<Suffix, Double> dmgAdds = kitPlayer.getAttackDamageAdds();
            HashMap<Suffix, Double> pResAdds = kitPlayer.getPhysicalResistAdds();

            double damage = kitPlayer.getAttackDamage();

            dmgAdds.put(this, -1 * (damage - 3));
            pResAdds.put(this, 0.8);
            kitPlayer.updateStats();

            addEffect(kitPlayer.getMcPlayer());

            SpongeUtils.getTaskBuilder().execute(() -> {
                dmgAdds.put(this, 0.0);
                pResAdds.put(this, 0.0);
                kitPlayer.updateStats();}).
                    delay(5, TimeUnit.SECONDS).
                    submit(SquareKit.getInstance());
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
        return Messages.getMessage("suffix-shelter")
                .replace("%MANACOST%", Formatters.round.format(manaCost))
                .replace("%COOLDOWN%", Formatters.round.format(cooldown/1000));
    }
}
