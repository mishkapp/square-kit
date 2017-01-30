package com.mishkapp.minecraft.plugins.squarekit.commands.bounty;

import com.mishkapp.minecraft.plugins.squarekit.BountyHandler;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;
import static java.lang.Math.max;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class ListBounty implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        List<KitPlayer> players = BountyHandler.getInstance().getList();

        Text.Builder b = Text.builder();
        b.color(TextColors.GOLD);
        b.append(Text.of("================================"));
        b.append(Text.NEW_LINE);

        AtomicInteger i = new AtomicInteger(1);
        players.forEach(p -> {
            b.append(Text.of("(" + i.get() + "): "));
            int treshold = p.getCurrentKit().getPrice() * 10;
            treshold = max(1000, treshold);

            b.append(Text.of(p.getMcPlayer().getName()));
            b.append(Text.of(" [" + p.getCurrentKillstreak() + "] " + p.getBounty() + "/" + treshold));

            if(p.getBounty() >= treshold){
                b.append(_text(" (" + p.getCurrentKit().getName() + "&6)"));
            }
            b.append(Text.NEW_LINE);
            i.addAndGet(1);
        });

        b.append(Text.of("================================"));

        src.sendMessage(b.build());
        return CommandResult.empty();
    }
}
