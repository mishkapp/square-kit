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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 06.01.2017.
 */
public class SetupCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            return CommandResult.empty();
        }
        Player player = (Player) src;
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);

        PlayerSettings settings = kitPlayer.getPlayerSettings();

        List<String> playerEntries = settings.getStatsPanelEntries();

        Text.Builder b = Text.builder();

        b.color(TextColors.GOLD);

        b.append(Text.of("================================"));
        b.append(Text.NEW_LINE);

        for(int i = 0; i < playerEntries.size(); i++){
            b.append(Text.of(
                    TextActions.showText(_text(Messages.get("stats.tip-up"))),
                    TextActions.runCommand("/statspanel up " + i),
                    TextColors.GREEN,
                    Text.of("[⬆]")));
            b.append(_text(" "));
            b.append(Text.of(
                    TextActions.showText(_text(Messages.get("stats.tip-down"))),
                    TextActions.runCommand("/statspanel down " + i),
                    TextColors.GREEN,
                    Text.of("[⬇]")));
            b.append(_text(" "));
            b.append(Text.of(
                    TextActions.showText(_text(Messages.get("stats.tip-remove"))),
                    TextActions.runCommand("/statspanel remove " + i),
                    TextColors.RED,
                    Text.of("[x]")));
            b.append(_text(" "));
            b.append(StatsEntries.getDescription(playerEntries.get(i)));
            b.append(Text.NEW_LINE);
        }
        b.append(Text.of(
                TextActions.showText(_text(Messages.get("stats.tip-add"))),
                TextActions.runCommand("/statspanel add"),
                TextColors.GREEN,
                Text.of("[+] ",
                        _text(Messages.get("stats.add-link")))));
        b.append(Text.NEW_LINE);

        b.append(Text.of("================================"));

        player.sendMessage(b.build());

        return CommandResult.empty();
    }
}
