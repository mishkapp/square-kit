package com.mishkapp.minecraft.plugins.squarekit.player;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 05.01.2017.
 */
public class PlayerSettings {

    private double healthScale = 1;
    private List<String> statsPanelEntries = new ArrayList<>();

    public PlayerSettings(){
        setDefaultPanelEntries();
    }

    private void setDefaultPanelEntries(){
        statsPanelEntries.add("physical-resist");
        statsPanelEntries.add("magic-resist");
        statsPanelEntries.add("mana");
        statsPanelEntries.add("money");
        statsPanelEntries.add("streak");
        statsPanelEntries.add("physical-resist");
        statsPanelEntries.add("level");
        statsPanelEntries.add("setup");
    }

    public double getHealthScale() {
        return healthScale;
    }

    public void setHealthScale(double healthScale) {
        this.healthScale = healthScale;
    }

    public List<String> getStatsPanelEntries() {
        return statsPanelEntries;
    }

    public void setStatsPanelEntries(List<String> statsPanelEntries) {
        this.statsPanelEntries = statsPanelEntries;
    }

    static PlayerSettings fromDocument(Document document){
        PlayerSettings result = new PlayerSettings();
        result.healthScale = document.getDouble("healthScale");
        if(document.containsKey("statsPanelEntries")){
            result.statsPanelEntries = (List<String>) document.get("statsPanelEntries");
        } else {
            result.setDefaultPanelEntries();
        }
        return result;
    }

    Document toDocument(){
        return new Document()
                .append("healthScale", healthScale)
                .append("statsPanelEntries", statsPanelEntries);
    }
}
