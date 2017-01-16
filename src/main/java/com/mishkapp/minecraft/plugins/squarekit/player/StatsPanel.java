package com.mishkapp.minecraft.plugins.squarekit.player;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import java.util.List;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 16.12.2016.
 */
public class StatsPanel {

    private Scoreboard scoreboard;
    private Objective statsObj;

    private KitPlayer kitPlayer;

    public StatsPanel(KitPlayer kitPlayer){
        this.kitPlayer = kitPlayer;
        scoreboard = Scoreboard.builder()
                .build();

        statsObj = Objective.builder()
                .name("stats")
                .criterion(Criteria.DUMMY)
                .displayName(_text(Messages.get("stats.panel-header")))
                .build();
        scoreboard.addObjective(statsObj);
        kitPlayer.getMcPlayer().setScoreboard(scoreboard);
        updateScoreboard();
    }

    public void update(){
        updateScoreboard();
    }

    private void updateScoreboard(){
        statsObj.getScores().forEach((t,s) -> statsObj.removeScore(s));
        List<String> entries = kitPlayer.getPlayerSettings().getStatsPanelEntries();
        int bound = Math.min(entries.size(), 16);
        for(int i = 0; i < bound; i++){
            statsObj.getOrCreateScore(getText(entries.get(i))).setScore(bound - i);
        }
        scoreboard.updateDisplaySlot(statsObj, DisplaySlots.SIDEBAR);
    }

    private Text getText(String key) {
        return StatsEntries.getText(key, kitPlayer);
    }
}
