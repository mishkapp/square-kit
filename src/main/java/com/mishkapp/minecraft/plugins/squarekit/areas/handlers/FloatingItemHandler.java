package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by mishkapp on 06.12.2016.
 */
public class FloatingItemHandler extends Handler {

    private final int LIVE_TIME = 300;
    private int step = 0;
    private Item item;

    private ItemStack itemStack;

    @Override
    public void tick(Area area) {
        if (step > 0){
            step -= 1;
            return;
        }
        step = LIVE_TIME;
        item = (Item) area.getWorld().createEntity(EntityTypes.ITEM, area.getCenter());
        item.tryOffer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        item.setVelocity(new Vector3d(0, 0, 0));
        item.offer(Keys.HAS_GRAVITY, false);
        item.offer(Keys.INFINITE_DESPAWN_DELAY, false);
        item.offer(Keys.PICKUP_DELAY, (LIVE_TIME * 2) * 20);
        item.getNearbyEntities(1).stream()
                .filter(e -> e != item && e instanceof Item)
                .forEach(Entity::remove);
        area.getWorld().spawnEntity(item,
                Cause.builder()
                .owner(SquareKit.getInstance())
                .build());
    }

    @Override
    public void remove(Area area){
        item.getNearbyEntities(1).stream()
                .filter(e -> e instanceof Item)
                .forEach(Entity::remove);
    }

    @Override
    public String serialize() {
        return "floating-item:" + itemStack.getItem().getName().split(":")[1];
    }

    public static FloatingItemHandler deserialize(String[] args){
        FloatingItemHandler result = new FloatingItemHandler();
        if(args.length > 0){
            result.itemStack = ItemStack.of(
                    Sponge.getGame().getRegistry().getType(ItemType.class, "minecraft:" + args[0]).orElse(ItemTypes.ANVIL),
                    1
            );
        }
        return result;
    }
}
