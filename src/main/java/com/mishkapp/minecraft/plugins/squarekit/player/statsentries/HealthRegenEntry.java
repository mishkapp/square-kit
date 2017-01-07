package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import org.spongepowered.api.text.Text;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 06.01.2017.
 */
public class HealthRegenEntry extends StreakEntry {
    @Override
    public Text getText(KitPlayer kitPlayer) {
        return _text(Messages.get("stats.health-regen")
                .replace("%HEALTH_REGEN%",
                        FormatUtils.tenth(kitPlayer.getHealthRegen() * 4)));
    }

    @Override
    public String getRawDescription() {
        return Messages.get("stats.health-regen-desc");
    }

}
