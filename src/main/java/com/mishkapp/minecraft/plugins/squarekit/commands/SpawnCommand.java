package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.ConfigProvider;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.configs.SpawnConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.scheduler.Task;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 07.12.2016.
 */
public class SpawnCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)){
            return CommandResult.empty();
        }
        Player player = (Player)src;

        if(player.hasPermission("squarekit.admin")){
            toSpawn(player);
        } else {
            toSpawn(player);
        }

        return CommandResult.empty();
    }

    public void toSpawnDelayed(final Player player){
        player.sendMessage(_text(Messages.get("warn-tp-wait")));
        Task task = Sponge.getScheduler().createTaskBuilder()
                .delayTicks(5 * 20)
                .execute(r -> {
                    toSpawn(player);

                })
                .submit(SquareKit.getInstance().getPlugin());

        Sponge.getEventManager().registerListeners(SquareKit.getInstance().getPlugin(), new TempListener(player, task));
    }

    public void toSpawn(Player player){
        SpawnConfig sc = ConfigProvider.getInstance().getSpawnConfig();
        player.transferToWorld(sc.getWorld(), sc.getLocation());
        player.setHeadRotation(sc.getRotation());
        player.sendMessage(_text(Messages.get("success-tp")));
    }

    public class TempListener {
        private Player player;
        private Task task;

        public TempListener(Player player, Task task) {
            this.player = player;
            this.task = task;
        }

        @Listener
        public void onMove(MoveEntityEvent event, @First Player p){
            if(p.equals(player)){
                p.sendMessage(_text(Messages.get("error-tp-moved")));
                task.cancel();
                Sponge.getEventManager().unregisterListeners(this);
            }
        }
    }
}
