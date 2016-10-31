package com.mishkapp.minecraft.plugins.squarekit.player;

import org.bson.Document;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class PlayerStats {
    private int maxKillstreak = 0;  //+
    private int kills = 0;          //+
    private int deaths = 0;         //+
    private double kdRatio = 0;     //+
    private int moneyEarned = 0;    //-
    private int moneySpent = 0;     //-

    public int getMaxKillstreak() {
        return maxKillstreak;
    }

    public void setMaxKillstreak(int maxKillstreak) {
        this.maxKillstreak = maxKillstreak;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public double getKdRatio() {
        return kdRatio;
    }

    public void setKdRatio(double kdRatio) {
        this.kdRatio = kdRatio;
    }

    public int getMoneyEarned() {
        return moneyEarned;
    }

    public void setMoneyEarned(int moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public int getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(int moneySpent) {
        this.moneySpent = moneySpent;
    }

    static PlayerStats fromDocument(Document document){
        PlayerStats result = new PlayerStats();
        result.kills = document.getInteger("kills");
        result.deaths = document.getInteger("deaths");
        result.kdRatio = document.getDouble("kdRatio");
        result.moneyEarned = document.getInteger("moneyEarned");
        result.moneySpent = document.getInteger("moneySpent");
        result.maxKillstreak = document.getInteger("maxKillstreak");
        return result;
    }

    Document toDocument(){
        return new Document("maxKillstreak", maxKillstreak)
                .append("kills", kills)
                .append("deaths", deaths)
                .append("kdRatio", kdRatio)
                .append("moneyEarned", moneyEarned)
                .append("moneySpent", moneySpent);
    }
}
