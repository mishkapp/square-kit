package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.ConfigProvider;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by mishkapp on 07.12.2016.
 */
public class SetSpawnCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player player = (Player)src;
            ConfigProvider.getInstance().getSpawnConfig().setWorld(player.getWorld());
            ConfigProvider.getInstance().getSpawnConfig().setLocation(player.getLocation().getPosition());
            ConfigProvider.getInstance().getSpawnConfig().setRotation(player.getHeadRotation());
            ConfigProvider.getInstance().saveSpawnConfig();
        }
        return CommandResult.empty();
    }
}
