package com.mishkapp.minecraft.plugins.squarekit.commands.area;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.AreaRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.areas.SphereArea;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class DefineSphereCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            String areaId = (String) args.getOne("areaId").get();
            String center = (String) args.getOne("center").get();
            String fi = (String) args.getOne("fi").get();
            String[] split = center.split(";");
            Vector3d vec1 = new Vector3d(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
            split = fi.split(";");
            Vector3d vec2 = new Vector3d(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));

            Area area = new SphereArea(areaId, player.getWorld().getName(), vec1, vec2);
            AreaRegistry.getInstance().add(area);
            area.save();
        }
        return CommandResult.empty();
    }
}
