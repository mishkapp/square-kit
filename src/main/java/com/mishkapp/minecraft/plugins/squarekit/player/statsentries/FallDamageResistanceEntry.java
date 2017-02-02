package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.text.Text;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 03.02.17.
 */
public class FallDamageResistanceEntry extends StatsEntry {
    @Override
    public Text getText(KitPlayer kitPlayer) {
        return _text(Messages.get("stats.fall-damage-resist")
                .replace("%RES%",
                        FormatUtils.unsignedRound(kitPlayer.getPhysicalResist() * 100)));
    }

    @Override
    public String getRawDescription() {
        return Messages.get("stats.fall-damage-resist-desc");
    }
}
