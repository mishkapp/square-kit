package com.mishkapp.minecraft.plugins.squarekit.commands.warp;

import com.mishkapp.minecraft.plugins.squarekit.WarpZonesRegistry;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class Tp implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player)src;
            String id = (String) args.getOne("id").get();
            List<WarpZonesRegistry.WarpPoint> points = WarpZonesRegistry.getInstance().getPoints(id);

            final List<WarpZonesRegistry.WarpPoint> warpPoints = points.parallelStream()
                    .sorted(Comparator.comparingInt(WarpZonesRegistry.WarpPoint::getNearbyPlayersCount))
                    .collect(Collectors.toList());
            List<WarpZonesRegistry.WarpPoint> warpPoints1 = warpPoints.parallelStream()
                    .filter(p -> p.getNearbyPlayersCount() == warpPoints.get(0).getNearbyPlayersCount())
                    .collect(Collectors.toList());
            WarpZonesRegistry.WarpPoint point = warpPoints1.get(new Random().nextInt(warpPoints1.size()));
            if(point != null){
                player.transferToWorld(point.getWorld(), point.getPosition());
            }
        }
        return CommandResult.empty();
    }
}
