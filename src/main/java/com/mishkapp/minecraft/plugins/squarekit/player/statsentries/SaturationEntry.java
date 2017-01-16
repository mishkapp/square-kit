package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.text.Text;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 17.01.2017.
 */
public class SaturationEntry extends StatsEntry {
    @Override
    public Text getText(KitPlayer kitPlayer) {
        return _text(Messages.get("stats.saturation")
                .replace("%SATURATION%",
                        FormatUtils.unsignedRound(kitPlayer.getMcPlayer().getFoodData().saturation().get())));
    }

    @Override
    public String getRawDescription() {
        return Messages.get("stats.saturation-desc");
    }
}
