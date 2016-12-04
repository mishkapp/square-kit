package com.mishkapp.minecraft.plugins.squarekit.commands.area;

import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class RemoveCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            String areaId = (String) args.getOne("areaId").get();
            AreaRegistry.getInstance().remove(areaId);
        }
        return CommandResult.empty();
    }
}
