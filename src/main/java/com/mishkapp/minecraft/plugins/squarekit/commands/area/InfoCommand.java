package com.mishkapp.minecraft.plugins.squarekit.commands.area;

import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.areas.CuboidArea;
import com.mishkapp.minecraft.plugins.squarekit.areas.SphereArea;
import com.mishkapp.minecraft.plugins.squarekit.areas.handlers.Handler;
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
 * Created by mishkapp on 04.12.2016.
 */
public class InfoCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            String areaId = (String) args.getOne("areaId").get();
            Area area = AreaRegistry.getInstance().get(areaId);

            Text.Builder b = Text.builder();

            b.color(TextColors.GOLD);

            b.append(Text.of("================================"));
            b.append(Text.NEW_LINE);

            b.append(Text.of("Area ID: "));
            b.append(Text.of(area.getId()));
            b.append(Text.NEW_LINE);

            b.append(Text.of("Is safe: "));
            b.append(Text.of(area.isSafe()));
            b.append(Text.NEW_LINE);

            if(area instanceof CuboidArea){
                b.append(Text.of("Def: "));
                b.append(Text.of("cuboid "));
                b.append(Text.of("[world: " + area.getWorld().getName()));
                b.append(Text.of(", min: "));
                b.append(Text.of(((CuboidArea) area).getAabb().getMin().toString()));
                b.append(Text.of(", max: "));
                b.append(Text.of(((CuboidArea) area).getAabb().getMax().toString()));
                b.append(Text.of("]"));
                b.append(Text.NEW_LINE);
            }
            if(area instanceof SphereArea){
                b.append(Text.of("Def: "));
                b.append(Text.of("sphere "));
                b.append(Text.of("[world: " + area.getWorld().getName()));
                b.append(Text.of(", center: "));
                b.append(Text.of(((SphereArea) area).getCenter().toString()));
                b.append(Text.of(", fi: "));
                b.append(Text.of(((SphereArea) area).getFi().toString()));
                b.append(Text.of("]"));
                b.append(Text.NEW_LINE);
            }

            b.append(Text.of("Handlers: "));
            b.append(Text.NEW_LINE);
            List<Handler> handlers = area.getHandlers();
            for(int i = 0; i < handlers.size(); i++){
                b.append(Text.of("    [" + i + "] "));
                b.append(Text.of(handlers.get(i).serialize()));
                b.append(Text.NEW_LINE);
            }
            b.append(Text.of("================================"));

            player.sendMessage(b.build());
        }
        return CommandResult.empty();
    }
}
