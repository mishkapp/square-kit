package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.player.LevelTable;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.text.Text;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 05.01.2017.
 */
public class ExpEntry extends StatsEntry {
    @Override
    public Text getText(KitPlayer kitPlayer) {
        return _text(Messages.get("stats.exp")
                .replace("%EXP%", FormatUtils.unsignedRound(kitPlayer.getExperience()))
                .replace("%MAX_EXP%", FormatUtils.unsignedRound(LevelTable.experiences[kitPlayer.getLevel() - 1])));
    }

    @Override
    public String getRawDescription() {
        return Messages.get("stats.exp-desc");
    }
}
