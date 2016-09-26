package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.ItemUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import static org.spongepowered.api.data.type.HandTypes.MAIN_HAND;
import static org.spongepowered.api.data.type.HandTypes.OFF_HAND;
import static org.spongepowered.api.item.ItemTypes.NONE;

/**
 * Created by mishkapp on 26.08.2016.
 */
public class LoreCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player player = (Player)src;
            System.out.println("player = " + player.getItemInHand(MAIN_HAND).get().toString());
            ItemStack i = player.getItemInHand(OFF_HAND).orElse(ItemStack.of(NONE, 1));
            if(i.getItem() == NONE){
                return CommandResult.empty();
            }
            ItemUtils.setLore(i, (String)args.getOne("lore").orElse(""));
        }
        return CommandResult.empty();
    }
}
