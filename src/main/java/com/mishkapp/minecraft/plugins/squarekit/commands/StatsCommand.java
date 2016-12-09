package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.player.LevelTable;
import com.mishkapp.minecraft.plugins.squarekit.player.PlayerStats;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

/**
 * Created by mishkapp on 05.11.2016.
 */
public class StatsCommand implements CommandExecutor {

    @Override
    @NonnullByDefault
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            return CommandResult.empty();
        }
        Player player = (Player)src;
        String targetName = args.getOne("player").orElse(player.getName()).toString();
        Player target = Sponge.getServer().getPlayer(targetName).orElse(null);
        if(target == null){
            player.sendMessage(Text.of("Игрок " + targetName + " не в сети"));
            return CommandResult.empty();
        }

        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(target.getUniqueId());
        PlayerStats playerStats = kitPlayer.getPlayerStats();

        Text.Builder b = Text.builder();

        b.color(TextColors.GOLD);

        b.append(Text.of("================================"));
        b.append(Text.NEW_LINE);

        b.append(Text.of("Игрок: "));
        b.append(target.getDisplayNameData().displayName().get());
        b.append(Text.NEW_LINE);

        b.append(Text.of("Ранг: "));
        b.append(Text.of(kitPlayer.getExperience()));
        b.append(Text.NEW_LINE);

        b.append(Text.of("Убийств: "));
        b.append(Text.of(playerStats.getKills()));
        b.append(Text.NEW_LINE);

        b.append(Text.of("Смертей: "));
        b.append(Text.of(playerStats.getDeaths()));
        b.append(Text.NEW_LINE);

        b.append(Text.of("KDR: "));
        b.append(Text.of(FormatUtils.unsignedHundredth(playerStats.getKdRatio())));
        b.append(Text.NEW_LINE);

        b.append(Text.of("Убийств подряд: "));
        b.append(Text.of(kitPlayer.getCurrentKillstreak()));
        b.append(Text.NEW_LINE);

        b.append(Text.of("Максимум убийств подряд: "));
        b.append(Text.of(playerStats.getMaxKillstreak()));
        b.append(Text.NEW_LINE);

        b.append(Text.of("Уровень: "));
        b.append(Text.of(kitPlayer.getLevel()));
        b.append(Text.NEW_LINE);

        b.append(Text.of("Опыт: "));
        b.append(Text.of(kitPlayer.getExperience() + "/" + LevelTable.experiences[kitPlayer.getLevel() - 1]));
        b.append(Text.NEW_LINE);

        b.append(Text.of("================================"));

        player.sendMessage(b.build());
        return CommandResult.empty();
    }
}
