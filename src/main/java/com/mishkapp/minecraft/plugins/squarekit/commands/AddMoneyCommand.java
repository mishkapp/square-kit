package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
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
public class AddMoneyCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int amount = (Integer) args.getOne("amount").get();

        Player player = (Player) args.getOne("player").get();

        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
        kitPlayer.addMoney(amount, false);

        src.sendMessage(_text("&aВы выдали игроку " + player.getName() + " деньги в размере " + amount));
        return CommandResult.empty();
    }
}
