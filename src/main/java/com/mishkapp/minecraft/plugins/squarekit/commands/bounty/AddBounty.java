package com.mishkapp.minecraft.plugins.squarekit.commands.bounty;

import com.mishkapp.minecraft.plugins.squarekit.KitRegistry;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
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
import static java.lang.Math.max;

/**
 * Created by mishkapp on 11.12.2016.
 */
public class AddBounty implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.empty();
        }
        Player player = (Player) src;

        Player targetPlayer = (Player) args.getOne("player").get();
        int bountyAdd = (int) args.getOne("bounty").get();

        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
        KitPlayer target = PlayersRegistry.getInstance().getPlayer(targetPlayer);

        if(kitPlayer.getMoney() < bountyAdd || bountyAdd < 0){
            player.sendMessage(_text(Messages.get("bounty.no-money")
            ));
            return CommandResult.empty();
        }

        int treshold = KitRegistry.getInstance().getKit(target.getCurrentKit()).getPrice() * 10;
        treshold = max(1000, treshold);

        if(target.getMoney() < treshold){
            player.sendMessage(_text(Messages.get("bounty.target-low-money")
                    .replace("%TARGET%", targetPlayer.getName())
            ));
            return CommandResult.empty();
        }

        kitPlayer.subtractMoney(bountyAdd, false);
        target.setBounty(target.getBounty() + bountyAdd);

        if(bountyAdd < 500){
            return CommandResult.empty();
        }
        Sponge.getServer().getOnlinePlayers().forEach(
                p -> {
                    p.sendMessage(_text(Messages.get("bounty.added")
                            .replace("%WHO%", player.getName())
                            .replace("%TARGET%", targetPlayer.getName())
                            .replace("%AMOUNT%", bountyAdd + "")
                            .replace("%BOUNTY%", target.getBounty() + "")
                    ));
                }
        );

        return CommandResult.empty();
    }
}
