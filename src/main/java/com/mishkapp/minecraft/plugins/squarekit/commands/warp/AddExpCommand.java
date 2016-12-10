package com.mishkapp.minecraft.plugins.squarekit.commands.warp;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 10.12.2016.
 */
public class AddExpCommand implements CommandExecutor{
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String playerName = (String) args.getOne("playerName").get();
        int amount = (Integer) args.getOne("amount").get();

        Player player = Sponge.getServer().getPlayer(playerName).orElse(null);

        if(player == null){
            src.sendMessage(_text("&4Игрок " + playerName + " не в сети"));
            return CommandResult.empty();
        }

        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player.getUniqueId());
        kitPlayer.addExp(amount);

        src.sendMessage(_text("&aВы выдали игроку " + playerName + " опыт в размере " + amount));
        return CommandResult.empty();
    }

}
