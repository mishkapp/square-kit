package com.mishkapp.minecraft.plugins.squarekit.suffixes.bow;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.InventoryUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.spongepowered.api.item.ItemTypes.ARROW;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class DeadlyArrow extends Suffix {

    private int time = 60;
    private int hpTreshold = 40;

    public DeadlyArrow(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if (event instanceof ArrowHitEntityEvent) {
            if(!isWeaponInHand()){
                return;
            }
            Entity entity = ((ArrowHitEntityEvent) event).getTarget();
            if(!(entity instanceof Living)){
                return;
            }

            Living living = (Living)entity;

            double hp = living.health().get();

            if(hp <= hpTreshold){
                living.damage(
                        hpTreshold,
                        EntityDamageSource.builder().entity(kitPlayer.getMcPlayer()).absolute().bypassesArmor().type(DamageTypes.PROJECTILE).build());
            } else {
                living.offer(Keys.GLOWING, true);
                kitPlayer.getMcPlayer().offer(Keys.GLOWING, true);

                Sponge.getScheduler().createTaskBuilder()
                        .execute(r -> {
                            living.offer(Keys.GLOWING, false);
                            kitPlayer.getMcPlayer().offer(Keys.GLOWING, false);
                        })
                        .delayTicks(time * 20)
                        .submit(SquareKit.getInstance().getPlugin());
            }

            Sponge.getScheduler().createTaskBuilder()
                    .execute(r -> InventoryUtils.addItem(kitPlayer.getMcPlayer(), ItemStack.of(ARROW, 1)))
                    .delayTicks(time * 20)
                    .submit(SquareKit.getInstance().getPlugin());

        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("deadly-arrow-suffix")
                .replace("%HPTRESHOLD%", String.valueOf(hpTreshold))
                .replace("%TIME%", String.valueOf(time));
    }
}
