package com.mishkapp.minecraft.plugins.squarekit.commands.statspanel;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.player.PlayerSettings;
import com.mishkapp.minecraft.plugins.squarekit.player.StatsEntries;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 06.01.2017.
 */
public class AddEntryCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            return CommandResult.empty();
        }
        Player player = (Player) src;
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
        String entry = (String) args.getOne("entry").get();

        PlayerSettings settings = kitPlayer.getPlayerSettings();

        List<String> playerEntries = settings.getStatsPanelEntries();


        if(playerEntries.size() == 16){
            player.sendMessage(_text(Messages.get("stats.error-max-entries-reached")));
            return CommandResult.empty();
        }

        if(playerEntries.contains(entry)){
            return CommandResult.empty();
        }

        playerEntries.add(entry);
        player.sendMessage(_text(Messages.get("stats.entry-added")
                .replace("%ENTRY%", StatsEntries.getRawDescription(entry))));

        return CommandResult.empty();
    }
}
