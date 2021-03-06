package com.mishkapp.minecraft.plugins.squarekit.commands.area;

import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by mishkapp on 04.12.2016.
 */
public class RemoveHandlerCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            String areaId = (String) args.getOne("areaId").get();
            Area area = AreaRegistry.getInstance().get(areaId);
            int id = (Integer) args.getOne("handlerId").get();
            area.removeHandler(id);
            area.save();
        }
        return CommandResult.empty();
    }
}
