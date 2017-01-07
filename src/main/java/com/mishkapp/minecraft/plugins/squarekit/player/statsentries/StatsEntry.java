package com.mishkapp.minecraft.plugins.squarekit.player.statsentries;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import org.spongepowered.api.text.Text;

import static com.mishkapp.minecraft.plugins.squarekit.utils.Utils._text;

/**
 * Created by mishkapp on 05.01.2017.
 */
public abstract class StatsEntry {

    public abstract Text getText(KitPlayer kitPlayer);

    public Text getDescription(){
        return _text(getRawDescription());
    }

    public abstract String getRawDescription();
}
