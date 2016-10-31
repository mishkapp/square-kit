package com.mishkapp.minecraft.plugins.squarekit.player;

import org.bson.Document;

import java.util.HashMap;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class KitsStats {
    private HashMap<String, KitStats> stats = new HashMap<>();

    public HashMap<String, KitStats> getStats() {
        return stats;
    }

    public KitStats get(String k){
        if(!stats.containsKey(k)){
            stats.put(k, new KitStats());
        }
        return stats.get(k);
    }

    public void setStats(HashMap<String, KitStats> stats) {
        this.stats = stats;
    }

    static KitsStats fromDocument(Document document){
        KitsStats result = new KitsStats();
        for(String s : document.keySet()){
            result.stats.put(s, KitStats.fromDocument((Document)document.get(s)));
        }
        return result;
    }

    Document toDocument(){
        Document result = new Document();
        for(String k : stats.keySet()){
            result.append(k, stats.get(k).toDocument());
        }
        return result;
    }

    static class KitStats {
        private int kills = 0;              //+
        private int deaths = 0;             //+
        private double kdRatio = 0;         //+
        private int maxKillstreak = 0;      //+
        private int moneyEarned = 0;        //-

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

        public int getMaxKillstreak() {
            return maxKillstreak;
        }

        public void setMaxKillstreak(int maxKillstreak) {
            this.maxKillstreak = maxKillstreak;
        }

        public int getMoneyEarned() {
            return moneyEarned;
        }

        public void setMoneyEarned(int moneyEarned) {
            this.moneyEarned = moneyEarned;
        }

        static KitStats fromDocument(Document document){
            KitStats result = new KitStats();
            result.kills = document.getInteger("kills");
            result.deaths = document.getInteger("deaths");
            result.kdRatio = document.getDouble("kdRatio");
            result.maxKillstreak = document.getInteger("maxKillstreak");
            result.moneyEarned = document.getInteger("moneyEarned");
            return result;
        }

        Document toDocument(){
            return new Document("maxKillstreak", maxKillstreak)
                    .append("kills", kills)
                    .append("deaths", deaths)
                    .append("kdRatio", kdRatio)
                    .append("moneyEarned", moneyEarned);
        }
    }
}
