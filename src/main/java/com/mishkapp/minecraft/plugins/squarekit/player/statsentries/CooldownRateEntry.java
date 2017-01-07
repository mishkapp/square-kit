package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.text.Text;

import java.util.Random;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 06.01.2017.
 */
public class CooldownRateEntry extends StatsEntry {
    @Override
    public Text getText(KitPlayer kitPlayer) {
        return _text(Messages.get("stats.cooldown-rate")
                .replace("%COOLDOWN_RATE%",
                        FormatUtils.unsignedRound(kitPlayer.getCooldownRate() * 100)));
    }

    @Override
    public String getRawDescription() {
        Random rand = new Random();
        rand.nextInt(1);
        return Messages.get("stats.cooldown-rate-desc");
    }
}
