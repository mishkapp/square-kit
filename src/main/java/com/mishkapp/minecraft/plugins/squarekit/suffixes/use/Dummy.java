package com.mishkapp.minecraft.plugins.squarekit.suffixes.use;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.EntityKilledEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.utils.DamageUtils.physicalDamage;
import static java.lang.Math.*;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class Dummy extends UseSuffix {
    private Human lastDummy = null;
    private int duration = 30;
    private int radius = 10;
    private int damage = 30;

    private ParticleEffect smoke = ParticleEffect.builder()
            .quantity(1)
            .type(ParticleTypes.FIRE_SMOKE)
            .build();

    private Random random = new Random();

    public Dummy(KitPlayer kitPlayer, ItemStack itemStack, Integer level) {
        super(kitPlayer, itemStack, level);

        cooldown = 30 * 1000;
        manaCost = 40;
    }

    @Override
    public void register() {}

    @Override
    public void handle(KitEvent event) {
        super.handle(event);
        if(event instanceof EntityKilledEvent){
            if(lastDummy != null){
                onDummyKilled(((EntityKilledEvent) event).getKiller());
            }
        }
        if(event instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemInHand(((ItemUsedEvent) event).getHandType())){
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

            if(lastDummy != null){
                lastDummy.remove();
                lastDummy = null;
            }

            createDummy();
        }
    }


    public void createDummy(){
        Player player = kitPlayer.getMcPlayer();
        World world = player.getWorld();
        Human dummy = (Human) world.createEntity(EntityTypes.HUMAN, player.getLocation().getPosition());
        dummy.setHelmet(player.getHelmet().orElse(null));
        player.setHelmet(null);
        dummy.setChestplate(player.getChestplate().orElse(null));
        player.setChestplate(null);
        dummy.setLeggings(player.getLeggings().orElse(null));
        player.setLeggings(null);
        dummy.setBoots(player.getBoots().orElse(null));
        player.setBoots(null);
        dummy.setCreator(player.getUniqueId());
        dummy.offer(Keys.DISPLAY_NAME, Text.of(player.getName()));
        dummy.offer(Keys.CUSTOM_NAME_VISIBLE, true);

        lastDummy = dummy;

        world.spawnEntity(
                dummy,
                Cause.builder()
                        .owner(SquareKit.getInstance())
                        .build()
        );

        Sponge.getScheduler().createTaskBuilder()
                .delayTicks(duration * 20)
                .execute(r -> {
                    if(dummy.isRemoved()){
                        return;
                    }
                    dummy.remove();
                })
                .submit(SquareKit.getInstance().getPlugin());
    }

    public void onDummyKilled(Entity killer){
        World world = lastDummy.getWorld();
        Collection<Entity> entities = lastDummy.getNearbyEntities(radius);
        entities.forEach(e -> {
            if(e == kitPlayer.getMcPlayer()){
                return;
            }
            e.damage(damage, physicalDamage(e));
        });
        addEffect(killer);

        for(int i = 0; i < 80; i++){
            world.spawnParticles(
                    smoke,
                    lastDummy.getLocation().getPosition()
                    .add(
                            radius * sin(random.nextGaussian()),
                            radius * sin(random.nextGaussian()),
                            radius * sin(random.nextGaussian())
                    )
            );
        }
        lastDummy = null;
    }

    private void addEffect(final Entity entity){
        final Vector3d loc = entity.getLocation().getPosition();
        final World world = entity.getWorld();
        world.playSound(SoundTypes.ENTITY_GENERIC_EXPLODE, lastDummy.getLocation().getPosition(), 4);

        for (int i = 0; i < 25; i++){
            double a = -1 * sin(random.nextDouble() * PI * 2);
            double b = cos(random.nextDouble() * PI * 2);

            Item item = createItem(loc.add(a, 2, b));

            item.setVelocity(new Vector3d(
                    0.2 * a,
                    0,
                    0.2 * b
            ));
            world.spawnEntity(
                    item,
                    Cause.builder()
                            .owner(SquareKit.getInstance())
                            .build()
            );

            Sponge.getScheduler().createTaskBuilder()
                    .execute(r -> item.remove())
                    .delayTicks(2 * 20)
                    .submit(SquareKit.getInstance().getPlugin());
        }
    }

    private Item createItem(Vector3d vec){
        switch (random.nextInt(5)){
            case 0:
                return createItem(ItemTypes.DIAMOND_SWORD, vec);
            case 1:
                return createItem(ItemTypes.GOLDEN_SWORD, vec);
            case 2:
                return createItem(ItemTypes.STONE_SWORD, vec);
            case 3:
                return createItem(ItemTypes.WOODEN_SWORD, vec);
            case 4:
                return createItem(ItemTypes.IRON_SWORD, vec);
            default:
                return createItem(ItemTypes.WOODEN_SWORD, vec);
        }
    }

    private Item createItem(ItemType itemType, Vector3d vec){
        Item result = (Item) kitPlayer.getMcPlayer().getWorld().createEntity(EntityTypes.ITEM, vec);
        result.tryOffer(Keys.REPRESENTED_ITEM, ItemStack.of(itemType, 1).createSnapshot());
        result.offer(Keys.PICKUP_DELAY, (4 * 2) * 20);
        return result;
    }

    @Override
    public String getLoreEntry() {
        return Messages.get("dummy-suffix")
                .replace("%DURATION%", duration + "")
                .replace("%DAMAGE%", damage + "")
                .replace("%RADIUS%", radius + "")
                + super.getLoreEntry();
    }
}
