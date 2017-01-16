package com.mishkapp.minecraft.plugins.squarekit.areas.handlers;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.PlayersRegistry;
import com.mishkapp.minecraft.plugins.squarekit.areas.Area;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 16.01.2017.
 */
public class BankHandler extends Handler {
    private ServerBossBar bossBar = ServerBossBar.builder()
            .color(BossBarColors.YELLOW)
            .overlay(BossBarOverlays.PROGRESS)
            .name(Text.of("BANK"))
            .percent(1.0f)
            .build();

    private double incomeValue = 1;
    private double incomeDelay = 1;
    private double maxCapacity = 100;
    private double outcomeValue = 1;
    private double outcomeDelay = 1;
    private boolean purge = false;

    private double currentValue = 0;
    private int incomeTick = 0;
    private int outcomeTick = 0;

    @Override
    public void tick(Area area) {
        bossBar.removePlayers(bossBar.getPlayers());
        List<Player> players = area.getPlayers();
        int onlinePlayers = Sponge.getServer().getOnlinePlayers().size();
        if(onlinePlayers < 10){
            if(purge){
                purge();
            }
            return;
        }
        bossBar.addPlayers(players);

        if(incomeTick < incomeDelay){
            incomeTick += 1;
        } else {
            incomeTick = 0;
            addMoney();
        }

        if(outcomeTick < outcomeDelay){
            outcomeTick += 1;
        } else {
            outcomeTick = 0;
            if(!players.isEmpty()){
                double withdrawal = withdrawMoney();
                players.forEach(p -> PlayersRegistry.getInstance().getPlayer(p).addMoney(withdrawal / players.size(), true));
            }
        }
        bossBar.setName(
                _text(Messages.get("areas.bank-bossbar-title")
                        .replace("%MONEY%", FormatUtils.unsignedRound(currentValue))));
        bossBar.setPercent((float) (currentValue / maxCapacity));
    }

    private void addMoney(){
        currentValue = Math.min(currentValue + incomeValue, maxCapacity);
    }

    private double withdrawMoney(){
        if(currentValue > outcomeValue){
            currentValue -= outcomeValue;
            return outcomeValue;
        } else {
            double result = currentValue;
            currentValue = 0;
            return result;
        }
    }

    private void purge(){
        currentValue = 0;
    }

    @Override
    public void remove(Area area){
        bossBar.removePlayers(bossBar.getPlayers());
    }

    @Override
    public String serialize() {
        return "bank:"
                + FormatUtils.unsignedTenth(incomeValue) + ":"
                + FormatUtils.unsignedTenth(incomeDelay) + ":"
                + FormatUtils.unsignedTenth(maxCapacity) + ":"
                + FormatUtils.unsignedTenth(outcomeValue) + ":"
                + FormatUtils.unsignedTenth(outcomeDelay) + ":"
                + purge;
    }

    public static BankHandler deserialize(String[] args){
        BankHandler result = new BankHandler();
        if(args.length > 0){
            result.incomeValue = Double.parseDouble(args[0]);
        }
        if(args.length > 1){
            result.incomeDelay = Double.parseDouble(args[1]) - 1;
        }
        if(args.length > 2){
            result.maxCapacity = Double.parseDouble(args[2]);
        }
        if(args.length > 3){
            result.outcomeValue = Double.parseDouble(args[3]);
        }
        if(args.length > 4){
            result.outcomeDelay = Double.parseDouble(args[4]) - 1;
        }
        if(args.length > 5){
            result.purge = Boolean.parseBoolean(args[5]);
        }
        return result;
    }
}
