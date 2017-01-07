package com.mishkapp.minecraft.plugins.squarekit.commands.statspanel;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.player.PlayerSettings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

/**
 * Created by mishkapp on 06.01.2017.
 */
public class RemoveEntryCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            return CommandResult.empty();
        }
        Player player = (Player) src;
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
        int index = (Integer) args.getOne("index").get();

        PlayerSettings settings = kitPlayer.getPlayerSettings();

        List<String> playerEntries = settings.getStatsPanelEntries();


        if(index < 0 || index >= playerEntries.size()){
            return CommandResult.empty();
        }

        playerEntries.remove(index);
        Sponge.getCommandManager().process(player, "statspanel setup");
        return CommandResult.empty();
    }
}
