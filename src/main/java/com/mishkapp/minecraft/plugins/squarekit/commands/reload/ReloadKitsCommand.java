package com.mishkapp.minecraft.plugins.squarekit.commands.reload;

import com.mishkapp.minecraft.plugins.squarekit.KitRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 10.12.2016.
 */
public class ReloadKitsCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder()
                .execute(r -> {
                    KitRegistry.getInstance().init();
                    src.sendMessage(_text("&aКиты были загружены"));
                })
                .async()
                .submit(SquareKit.getInstance().getPlugin());

        return CommandResult.empty();
    }
}
