package com.mishkapp.minecraft.plugins.squarekit.commands.area;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.areas.CuboidArea;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class DefineCuboidCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            String areaId = (String) args.getOne("areaId").get();

            String min = (String) args.getOne("min").get();
            String max = (String) args.getOne("max").get();
            String[] split = min.split(";");
            Vector3d vec1 = new Vector3d(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
            split = max.split(";");
            Vector3d vec2 = new Vector3d(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));

            Area area = new CuboidArea(areaId, player.getWorld().getName(), vec1, vec2);
            AreaRegistry.getInstance().add(area);
            area.save();
            player.sendMessage(Text.of("Area " + areaId + " created"));
        }
        return CommandResult.empty();
    }
}
