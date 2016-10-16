package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.Kit;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by mishkapp on 16.10.2016.
 */
public class KitsCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player player = (Player)src;
            Text.Builder builder = Text.builder();
            for(String k : SquareKit.getKitRegistry().getKitList()){
                Kit kit = SquareKit.getKitRegistry().getKit(k);
                builder.append(Text.of(
                        TextActions.showText(TextSerializers.FORMATTING_CODE.deserialize(kit.getDescription())),
                        TextActions.runCommand("/kit " + k), TextSerializers.FORMATTING_CODE.deserialize(kit.getName() + " ")));
            }
            player.sendMessage(builder.build());
        }
        return CommandResult.empty();
    }
}
