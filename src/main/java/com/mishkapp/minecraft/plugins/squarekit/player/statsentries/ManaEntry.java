package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.text.Text;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 05.01.2017.
 */
public class ManaEntry extends StatsEntry {
    @Override
    public Text getText(KitPlayer kitPlayer) {
        return _text(Messages.get("stats.mana")
                .replace("%MANA%",
                        FormatUtils.unsignedRound(kitPlayer.getCurrentMana()))
                .replace("%MAX_MANA%",
                        FormatUtils.unsignedRound(kitPlayer.getMaxMana())));
    }

    @Override
    public String getRawDescription() {
        return Messages.get("stats.mana-desc");
    }
}
