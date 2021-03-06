package com.mishkapp.minecraft.plugins.squarekit.effects;

import com.mishkapp.minecraft.plugins.squarekit.player.KitPlayer;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;

/**
 * Created by mishkapp on 09.12.2016.
 */
public abstract class Effect {

    protected KitPlayer kitPlayer;
    protected Suffix source;
    protected int level;
    protected long duration;

    protected long startTime;

    protected boolean running = true;

    public Effect(KitPlayer kitPlayer, Suffix source, int level, long duration) {
        this.kitPlayer = kitPlayer;
        this.source = source;
        this.level = level;
        this.duration = duration;

        startTime = System.currentTimeMillis();
    }

    public KitPlayer getKitPlayer() {
        return kitPlayer;
    }

    public Suffix getSource() {
        return source;
    }

    public int getLevel() {
        return level;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void tick(){
        if((System.currentTimeMillis() - startTime) >= duration){
            running = false;
        }
    }
}
