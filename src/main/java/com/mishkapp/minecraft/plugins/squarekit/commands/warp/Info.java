package com.mishkapp.minecraft.plugins.squarekit.commands.warp;

import com.mishkapp.minecraft.plugins.squarekit.WarpZonesRegistry;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class Info implements CommandExecutor{
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            String id = (String) args.getOne("id").get();

            List<WarpZonesRegistry.WarpPoint> points = WarpZonesRegistry.getInstance().getPoints(id);

            Text.Builder b = Text.builder();

            b.color(TextColors.GOLD);

            b.append(Text.of("================================"));
            b.append(Text.NEW_LINE);

            b.append(Text.of("Warp ID: "));
            b.append(Text.of(id));
            b.append(Text.NEW_LINE);

            b.append(Text.of("Points: "));
            b.append(Text.NEW_LINE);
            for(int i = 0; i < points.size(); i++){
                b.append(Text.of("    [" + i + "] "));
                b.append(Text.of(points.get(i).getWorld().getName() + " " + points.get(i).getPosition()));
                b.append(Text.NEW_LINE);
            }
            b.append(Text.of("================================"));

            player.sendMessage(b.build());
        }
        return CommandResult.empty();
    }
}
