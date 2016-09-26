package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.Formatters;
import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Use;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.spongepowered.api.block.BlockTypes.*;

/**
 * Created by mishkapp on 11.05.2016.
 */
public class Thunderbolt extends Use {

    private Set<BlockType> transparentBlocks = new HashSet<>();
    private double manaCost;

    public Thunderbolt(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        manaCost = 1024 - (level * 0.25);
        Collections.addAll(transparentBlocks,
                AIR, GRASS, LEAVES, LEAVES2
        );

    }

    @Override
    protected boolean isItemPresent(Player player) {
        return false;
    }

    @Override
    public void register(KitPlayer player) {

    }

    @Override
    public void handle(KitEvent e, KitPlayer kitPlayer) {
        if(e instanceof ItemUsedEvent){
            Player player = kitPlayer.getMcPlayer();

            if(!isItemPresentInHand(player)){
                return;
            }

            double currentMana = kitPlayer.getCurrentMana();
            if(currentMana < manaCost){
                return;
            }
            kitPlayer.setCurrentMana(currentMana - manaCost);

            BlockRay<World> blockRay = BlockRay.from(player)
                    .blockLimit(15)
                    .filter(BlockRay.continueAfterFilter(
                            o -> transparentBlocks.contains(o.getExtent().getBlock(o.getBlockPosition()).getType()), 1)
                    )
                    .build();
            BlockRayHit blockRayHit = blockRay.end().orElse(null);
            if(blockRayHit != null){
                World world = (World) blockRayHit.getExtent();
                world.spawnEntity(
                        world.createEntity(EntityTypes.LIGHTNING, blockRayHit.getBlockPosition()),
                        Cause.of(NamedCause.owner(SquareKit.getInstance().getPlugin()))
                );
            }
        }
    }

    @Override
    public String getLoreEntry() {
        return Messages.getMessage("suffix-thunderbolt").replace("%MANACOST%", Formatters.hundredth.format(manaCost));
    }
}
