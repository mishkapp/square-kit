package com.mishkapp.minecraft.plugins.squarekit.suffixes.bow;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.ArrowHitEntityEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.SuffixTickEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mishkapp.minecraft.plugins.squarekit.utils.InventoryUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import static org.spongepowered.api.item.ItemTypes.ARROW;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class DeadlyArrow extends Suffix {

    private double cooldown = 60;
    private int hpTreshold = 40;

    private long lastUse;

    public DeadlyArrow(KitPlayer kitPlayer, ItemStack itemStack, String[] args) {
        super(kitPlayer, itemStack, args);
        if(args.length > 0){
            cooldown = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            hpTreshold = Integer.parseInt(args[1]);
        }

    }

    protected boolean isCooldowned(KitPlayer kitPlayer){
        long delta = System.currentTimeMillis() - lastUse;
        if(delta < (cooldown * kitPlayer.getCooldownRate())){
            double time = ((cooldown * kitPlayer.getCooldownRate()) - delta)/1000.0;
            kitPlayer.getMcPlayer().sendMessage(
                    TextSerializers.FORMATTING_CODE.deserialize(
                            Messages.get("alert.cooldown")
                                    .replace("%TIME%", FormatUtils.unsignedTenth(time)))
            );
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        if(event instanceof SuffixTickEvent){
            if(!isItemInHand()){
                return;
            }

            if(!isCooldowned(kitPlayer)){
                return;
            }
            int barSize = 50;
            long delta = System.currentTimeMillis() - lastUse;
            double time = ((cooldown * kitPlayer.getCooldownRate()) - delta)/1000.0;
            double ratio = time/(cooldown/1000);
            ratio = Math.max(0.0, ratio);
            time = Math.max(0.0, time);
            int barChars = (int) (ratio * barSize);
            String cooldownBar = "[";
            cooldownBar += StringUtils.repeat('|', barSize - barChars);
            cooldownBar += StringUtils.repeat('.', barChars);
            cooldownBar += "] ";
            cooldownBar += "(" + FormatUtils.unsignedTenth(time) + "c.)";

            TextColor barColor;
            if(ratio <= 0.0){
                barColor = TextColors.BLUE;
            } else {
                barColor = TextColors.GOLD;
            }

            kitPlayer.getMcPlayer().sendMessage(ChatTypes.ACTION_BAR,
                    Text.builder(cooldownBar).color(barColor).build());
        }
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
                        .delayTicks((long) (cooldown * 20))
                        .submit(SquareKit.getInstance().getPlugin());
            }

            Sponge.getScheduler().createTaskBuilder()
                    .execute(r -> InventoryUtils.addItem(kitPlayer.getMcPlayer(), ItemStack.of(ARROW, 1)))
                    .delayTicks((long) (cooldown * 20))
                    .submit(SquareKit.getInstance().getPlugin());
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("suffix.deadly-arrow")
                .replace("%HPTRESHOLD%", String.valueOf(hpTreshold))
                .replace("%TIME%", FormatUtils.unsignedRound(cooldown));
    }
}
