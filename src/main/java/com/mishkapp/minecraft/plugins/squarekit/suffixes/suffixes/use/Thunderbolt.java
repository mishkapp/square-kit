package com.mishkapp.minecraft.plugins.squarekit.suffixes.suffixes.use;

import com.mishkapp.minecraft.plugins.squarekit.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.events.ItemUsedEvent;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Use;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.spongepowered.api.block.BlockTypes.*;

/**
 * Created by mishkapp on 11.05.2016.
 */
public class Thunderbolt extends Use {

    private Set<BlockType> transparentBlocks = new HashSet<>();
    private float manaCost;

    public Thunderbolt(ItemStack itemStack, Integer level) {
        super(itemStack, level);
        manaCost = 1.0F/level;
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
            //TODO: MANA
//            float currentMana = player.getExp();
//            if(currentMana < manaCost){
//                return;
//            }
//            player.setExp(currentMana - manaCost);
//            Block block = player.getPlayer().getTargetBlock(transparentBlocks, 30);
//            block.getWorld().strikeLightning(block.getLocation());
        }
    }

    @Override
    public String getLoreEntry() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return "Призывает молнию с небес. §9" + formatter.format(manaCost) + "mp";
    }
}
