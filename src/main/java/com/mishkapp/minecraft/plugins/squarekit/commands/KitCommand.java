package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.Kit;
import com.mishkapp.minecraft.plugins.squarekit.KitRegistry;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by mishkapp on 21.05.2016.
 */
public class KitCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player player = (Player)src;
            String s = (String)args.getOne("kitId").orElse("");
            Kit kit = KitRegistry.getInstance().getKit(s);
            kit.applyToPlayer(player);
        }
        return CommandResult.empty();
    }
}
