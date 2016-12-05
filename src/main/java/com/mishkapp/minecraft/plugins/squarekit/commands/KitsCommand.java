package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

/**
 * Created by mishkapp on 16.10.2016.
 */
public class KitsCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player player = (Player)src;
            KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player.getUniqueId());
            Inventory inventory = Inventory.builder()
                    .property(InventoryDimension.PROPERTY_NAM, InventoryDimension.of(9, 6))
                    .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("Киты")))
                    .listener(
                            ClickInventoryEvent.class,
                            e -> {
                                System.out.println("e = " + e);
                            }
                    )
                    .build(SquareKit.getInstance().getPlugin());
//
//            int i = 0;
//            for(String k : SquareKit.getKitRegistry().getKitList()){
//                Kit kit = SquareKit.getKitRegistry().getKit(k);
//                ItemStack menuItem = kit.getItemForMenu();
//                if(kitPlayer.getMoney() < kit.getPrice()){
//                    menuItem.setQuantity(0);
//                }
//
//
//                inventory.offer(menuItem);
//                inventory.query()
////                inventory.(new SlotPos(i/9, i%9));
//                i += 1;
//            }
            System.out.println("inventory = " + inventory);

            System.out.println("inventory = " + inventory.slots());

            player.openInventory(
                    inventory,
                    Cause.of(NamedCause.source(SquareKit.getInstance().getPlugin())));
        }
        return CommandResult.empty();
    }
}
