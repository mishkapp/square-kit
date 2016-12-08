package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.utils.InventoryUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;

import static java.lang.Math.floor;
import static java.lang.Math.min;
import static org.spongepowered.api.item.ItemTypes.APPLE;
import static org.spongepowered.api.text.format.TextColors.GREEN;

/**
 * Created by mishkapp on 08.12.2016.
 */
public class FeedHandler extends Handler{
    private ServerBossBar bossBar = ServerBossBar.builder()
            .color(BossBarColors.GREEN)
            .overlay(BossBarOverlays.PROGRESS)
            .name(Text.of("FOOD"))
            .percent(1.0f)
            .build();

    private int baseTime;
    private int foodAdd;

    private int currentTick = 0;

    public FeedHandler() {
        foodAdd = 1;
        baseTime = 4;
    }

    @Override
    public void tick(Area area) {
        bossBar.removePlayers(bossBar.getPlayers());
        List<Player> players = area.getPlayers();
        if(players.isEmpty() || Sponge.getServer().getOnlinePlayers().size() < 1){
            return;
        }
        bossBar.addPlayers(players);

        int realTime = (int) floor(baseTime * (1 + (0.25 * (players.size() - 1))));

        if(currentTick < realTime){
            currentTick += 1;
        } else {
            players.forEach(p -> {
                int food = p.foodLevel().get();
                if(food == 20){
                    int applesCount = InventoryUtils.countItems(p.getInventory(), APPLE);

                    p.sendMessage(Text.of("" + applesCount));
                    if (applesCount < 10) {
                        InventoryUtils.addItem(p, ItemStack.of(APPLE, 1));
//                        InventoryTransactionResult res = pInv.getHotbar().offer(ItemStack.of(APPLE, 1));
                    }
                } else {
                    p.offer(Keys.FOOD_LEVEL, min(food + foodAdd, 20));
                }
            });
            currentTick = 0;
        }
        bossBar.setName(Text.builder().color(GREEN).append(Text.of("Еда будет через " + (realTime - currentTick) + "c.")).build());
        bossBar.setPercent((float) ((double)currentTick/((double)realTime)));
    }

    @Override
    public void remove(Area area){
        bossBar.removePlayers(bossBar.getPlayers());
    }

    @Override
    public String serialize() {
        return "feed:" + foodAdd + ":" + baseTime;
    }

    public static FeedHandler deserialize(String[] args){
        FeedHandler result = new FeedHandler();
        if(args.length > 1){
            result.foodAdd = Integer.parseInt(args[0]);
            result.baseTime = Integer.parseInt(args[1]);
        }
        return result;
    }
}
