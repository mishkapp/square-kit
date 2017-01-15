package com.mishkapp.minecraft.plugins.squarekit.commands;

import com.mishkapp.minecraft.plugins.squarekit.Kit;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.comparators.KitComparator;
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
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

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
            KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
            if(!kitPlayer.isInSafeZone()){
                player.sendMessage(_text(Messages.get("kit-bad-area")));
                return CommandResult.empty();
            }

            List<Kit> kits = SquareKit.getKitRegistry().getKitList().stream()
                    .filter(k -> (
                            player.hasPermission(k.getPermission())
                                    || (!player.hasPermission(k.getPermission()) && k.getConditionMessage() != null)))
                    .sorted(new KitComparator())
                    .collect(Collectors.toList());

            final HashMap<ItemStack, Kit> itemKitMap = new HashMap<>();

            Inventory inventory = Inventory.builder()
                    .of(InventoryArchetypes.DOUBLE_CHEST)
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
                                Set<ItemStack> keys = itemKitMap.keySet();
                                List<ItemStack> applicableKeys = keys.stream()
                                        .filter(i -> {
                                            for (SlotTransaction t : transactions) {
                                                if (ItemUtils.isSimilar(i, t.getOriginal().createStack())) {
                                                    return true;
                                                }
                                            }
                                            return false;
                                        }).collect(Collectors.toList());
                                if (applicableKeys.size() > 0) {
                                    applyKit(p, itemKitMap.get(applicableKeys.get(0)));
                                }
                                p.closeInventory(Cause.of(NamedCause.source(SquareKit.getInstance().getPlugin())));
                            }
                    )
                    .build(SquareKit.getInstance().getPlugin());

            GridInventory gridInventory = inventory.query(GridInventory.class);
            int i = 0;
            for(Kit k : kits){
                ItemStack menuItem = k.getMenuItem();
                if(kitPlayer.getMoney() < k.getPrice() || kitPlayer.getLevel() < k.getMinLevel() || !kitPlayer.getMcPlayer().hasPermission(k.getPermission())){
                    menuItem = ItemStack.builder().from(menuItem).itemType(ItemTypes.BARRIER).build();
                }
                itemKitMap.put(menuItem.copy(), k);
                gridInventory.offer(menuItem);
                i += 1;
            }

            player.openInventory(
                    inventory,
                    Cause.of(NamedCause.source(SquareKit.getInstance().getPlugin())));
        }
        return CommandResult.empty();
    }

    private void applyKit(Player player, Kit kit){
        KitPlayer kitPlayer = PlayersRegistry.getInstance().getPlayer(player);
        if(!kitPlayer.getMcPlayer().hasPermission(kit.getPermission())){
            player.sendMessage(_text(kit.getConditionMessage()));
            return;
        }

        if(kitPlayer.getMoney() < kit.getPrice()){
            player.sendMessage(_text(Messages.get("kit-too-expensive").replace("%KIT%", kit.getName())));
            return;
        }

        if(kitPlayer.getLevel() < kit.getMinLevel()){
            player.sendMessage(_text(Messages.get("kit-level-too-small").replace("%KIT%", kit.getName())));
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
