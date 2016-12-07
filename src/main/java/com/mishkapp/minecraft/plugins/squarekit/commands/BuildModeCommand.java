package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Created by mishkapp on 07.12.2016.
 */
public class BuildModeCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player player = (Player)src;
            KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player.getUniqueId());
            kitPlayer.setInBuildMode(!kitPlayer.isInBuildMode());

            if(kitPlayer.isInBuildMode()){
                player.sendMessage(Text.of("You are now in build mode"));
            } else {
                player.sendMessage(Text.of("You are no longer in build mode"));
            }
        }
        return CommandResult.empty();
    }
}
