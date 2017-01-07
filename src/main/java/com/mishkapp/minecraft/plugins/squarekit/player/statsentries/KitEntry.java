package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.KitRegistry;
import com.mishkapp.minecraft.plugins.squarekit.Messages;
import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.text.Text;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 06.01.2017.
 */
public class KitEntry extends StatsEntry {
    @Override
    public Text getText(KitPlayer kitPlayer) {
        return _text(Messages.get("stats.kit")
                .replace("%KIT%",
                        KitRegistry.getInstance().getKit(kitPlayer.getCurrentKit()).getName()));
    }

    @Override
    public String getRawDescription() {
        return Messages.get("stats.kit-desc");
    }
}
