package com.mishkapp.minecraft.plugins.squarekit.commands.reload;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 10.12.2016.
 */
public class ReloadMessagesCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Messages.init();
        src.sendMessage(_text("&aСообщения были загружены"));
        return CommandResult.empty();
    }
}
