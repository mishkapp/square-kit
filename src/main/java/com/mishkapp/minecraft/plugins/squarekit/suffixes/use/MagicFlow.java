package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedOnTargetEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Random;

/**
 * Created by mishkapp on 13.12.2016.
 */
public class MagicFlow extends UseSuffix {
    private double damage = 10.0;
    private int time = 10;

    private double speedReduction = -0.3;
    private double manaReduction = 0.3;

    private ParticleEffect trailEffect = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.DRIP_WATER)
            .build();

    private Random random = new Random();

    public MagicFlow(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 7 * 1000;
        manaCost = 0 - (level * 64.0/0);
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof ItemUsedOnTargetEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
                return;
            }

            if(!(((ItemUsedOnTargetEvent) event).getTarget() instanceof Player)){
                return;
            }

            if(((ItemUsedOnTargetEvent) event).getTarget().getLocation().getPosition().distance(player.getLocation().getPosition()) > 10){
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

            KitPlayer target = PlayersRegistry.getInstance().getPlayer(((ItemUsedOnTargetEvent) event).getTarget().getUniqueId());

            double mana = target.getCurrentMana();

            if(mana == 0){
                target.getSpeedAdds().put(this, speedReduction);
                Sponge.getScheduler().createTaskBuilder()
                        .delayTicks(5 * 20)
                        .execute(r -> target.getSpeedAdds().remove(this));
            } else {
                target.setCurrentMana(mana * (1 - manaReduction));
                kitPlayer.setCurrentMana(kitPlayer.getCurrentMana() + mana * manaReduction);
            }

        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("magic-flow-suffix")
                .replace("%DAMAGE%", FormatUtils.unsignedRound(damage))
                .replace("%TIME%", FormatUtils.unsignedTenth(time))
                + super.getLoreEntry();
    }
}
