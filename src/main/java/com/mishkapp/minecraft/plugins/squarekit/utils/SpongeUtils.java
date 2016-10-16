package com.mishkapp.minecraft.plugins.squarekit.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

/**
 * Created by mishkapp on 15.08.2016.
 */
public class SpongeUtils {

    public static Task.Builder getTaskBuilder(){
        return Sponge.getScheduler().createTaskBuilder();
    }
}
