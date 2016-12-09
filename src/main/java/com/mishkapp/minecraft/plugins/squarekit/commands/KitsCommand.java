package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.Kit;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.ItemUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 16.10.2016.
 */
public class KitsCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player player = (Player)src;

            List<Kit> kits = SquareKit.getKitRegistry().getKitList().stream()
                    .filter(k -> player.hasPermission(k.getPermission()))
                    .sorted(Comparator.comparingInt(Kit::getPrice))
                    .collect(Collectors.toList());

            HashMap<ItemStack, Kit> itemKitMap = new HashMap<>();

            KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player.getUniqueId());
            Inventory inventory = Inventory.builder()
                    .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, 6))
                    .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("Киты")))
                    .listener(
                            ClickInventoryEvent.class,
                            e -> {
                                e.setCancelled(false);
                                Player p = e.getCause().first(Player.class).orElse(null);
                                if(player != p){
                                    return;
                                }
                                List<SlotTransaction> transactions = e.getTransactions();
                                ItemStackSnapshot t = transactions.get(0).getOriginal();

                                Set<ItemStack> keys = itemKitMap.keySet();

                                List<ItemStack> appliableKeys = keys.stream().filter(i -> ItemUtils.isSimilar(i, t.createStack())).collect(Collectors.toList());
                                p.closeInventory(Cause.of(NamedCause.source(SquareKit.getInstance().getPlugin())));
                                if(appliableKeys.size() > 0){
                                    applyKit(p, itemKitMap.get(appliableKeys.get(0)));
                                }
                            }
                    )
                    .build(SquareKit.getInstance().getPlugin());

            GridInventory gridInventory = inventory.query(GridInventory.class);
            int i = 0;
            for(Kit k : kits){
                ItemStack menuItem = k.getMenuItem();
                if(kitPlayer.getMoney() < k.getPrice()){
                    menuItem = ItemStack.builder().from(menuItem).itemType(ItemTypes.BARRIER).build();
                }
                itemKitMap.put(menuItem, k);
                gridInventory.set(i%9, i/9, menuItem);
                i += 1;
            }

            player.openInventory(
                    inventory,
                    Cause.of(NamedCause.source(SquareKit.getInstance().getPlugin())));
        }
        return CommandResult.empty();
    }

    private void applyKit(Player player, Kit kit){
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player.getUniqueId());
        if(kitPlayer.getMoney() < kit.getPrice()){
            player.sendMessage(_text(Messages.get("kit-too-expensive").replace("%KIT%", kit.getName())));
            return;
        }

        if(kitPlayer.getCurrentKit().equals(kit.getId())){
            player.sendMessage(_text(Messages.get("kit-already-picked").replace("%KIT%", kit.getName())));
            return;
        }

        kitPlayer.subtractMoney(kit.getPrice(), true);
        kit.applyToPlayer(kitPlayer);
        player.sendMessage(_text(Messages.get("kit-purchased").replace("%KIT%", kit.getName())));
    }
}
