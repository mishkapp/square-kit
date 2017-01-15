package com.mishkapp.minecraft.plugins.squarekit.commands.statspanel;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.player.StatsEntries;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 06.01.2017.
 */
public class AddCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
        List<String> playerEntries = kitPlayer.getPlayerSettings().getStatsPanelEntries();

        String[] entries = StatsEntries.getRegisteredEntries();

        Text.Builder b = Text.builder();
        b.color(TextColors.GOLD);

        b.append(Text.of("================================"));
        b.append(Text.NEW_LINE);

        for(int i = 0; i < entries.length; i++){
            if(playerEntries.contains(entries[i])){
                continue;
            }
            b.append(Text.of(
                    TextActions.showText(_text(Messages.get("stats.tip-add"))),
                    TextActions.runCommand("/statspanel addentry " + entries[i]),
                    StatsEntries.getDescription(entries[i])));
            b.append(Text.NEW_LINE);
        }

        b.append(Text.of(
                TextActions.showText(_text(Messages.get("stats.back-to-setup-tip"))),
                TextActions.runCommand("/statspanel setup"),
                TextColors.GREEN,
                Text.of("[<] ",
                        _text(Messages.get("stats.back-to-setup")))));
        b.append(Text.NEW_LINE);

        b.append(Text.of("================================"));

        player.sendMessage(b.build());

        return CommandResult.empty();
    }
}
