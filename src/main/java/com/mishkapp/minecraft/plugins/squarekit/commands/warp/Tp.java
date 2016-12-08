package com.mishkapp.minecraft.plugins.squarekit.commands.warp;

import com.mishkapp.minecraft.plugins.squarekit.WarpZonesRegistry;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class Tp implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player)src;
            String id = (String) args.getOne("id").get();
            WarpZonesRegistry.getInstance().warp(player, id);
        }
        return CommandResult.empty();
    }
}
