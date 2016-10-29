package com.mishkapp.minecraft.plugins.squarekit.player;

import com.mongodb.BasicDBObject;

import java.util.HashMap;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class KitsStats {
    private HashMap<String, KitStats> stats = new HashMap<>();

    public HashMap<String, KitStats> getStats() {
        return stats;
    }

    public void setStats(HashMap<String, KitStats> stats) {
        this.stats = stats;
    }

    static KitsStats fromDBObject(BasicDBObject obj){
        KitsStats result = new KitsStats();
        for(String s : obj.keySet()){
            result.stats.put(s, KitStats.fromDBOBject((BasicDBObject)obj.get(s)));
        }
        return result;
    }

    BasicDBObject toDBOject(){
        BasicDBObject result = new BasicDBObject();
        for(String k : stats.keySet()){
            result.append(k, stats.get(k).toDBObject());
        }
        return result;
    }

    static class KitStats {
        private int kills = 0;
        private int deaths = 0;
        private double kdRatio = 0;
        private int maxKillstreak = 0;
        private double physicalDamageTaken = 0;
        private double physicalDamageDealt = 0;
        private double magicDamageTaken = 0;
        private double magicDamageDealt = 0;
        private double pureDamageTaken = 0;
        private double pureDamageDealt = 0;
        private int moneyEarned = 0;

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

        public double getPhysicalDamageTaken() {
            return physicalDamageTaken;
        }

        public void setPhysicalDamageTaken(double physicalDamageTaken) {
            this.physicalDamageTaken = physicalDamageTaken;
        }

        public double getPhysicalDamageDealt() {
            return physicalDamageDealt;
        }

        public void setPhysicalDamageDealt(double physicalDamageDealt) {
            this.physicalDamageDealt = physicalDamageDealt;
        }

        public double getMagicDamageTaken() {
            return magicDamageTaken;
        }

        public void setMagicDamageTaken(double magicDamageTaken) {
            this.magicDamageTaken = magicDamageTaken;
        }

        public double getMagicDamageDealt() {
            return magicDamageDealt;
        }

        public void setMagicDamageDealt(double magicDamageDealt) {
            this.magicDamageDealt = magicDamageDealt;
        }

        public double getPureDamageTaken() {
            return pureDamageTaken;
        }

        public void setPureDamageTaken(double pureDamageTaken) {
            this.pureDamageTaken = pureDamageTaken;
        }

        public double getPureDamageDealt() {
            return pureDamageDealt;
        }

        public void setPureDamageDealt(double pureDamageDealt) {
            this.pureDamageDealt = pureDamageDealt;
        }

        public int getMoneyEarned() {
            return moneyEarned;
        }

        public void setMoneyEarned(int moneyEarned) {
            this.moneyEarned = moneyEarned;
        }

        static KitStats fromDBOBject(BasicDBObject obj){
            KitStats result = new KitStats();
            result.kills = obj.getInt("kills");
            result.deaths = obj.getInt("deaths");
            result.kdRatio = obj.getDouble("kdRatio");
            result.physicalDamageTaken = obj.getDouble("physicalDamageTaken");
            result.physicalDamageDealt = obj.getDouble("physicalDamageDealt");
            result.magicDamageTaken = obj.getDouble("magicDamageTaken");
            result.magicDamageDealt = obj.getDouble("magicDamageDealt");
            result.pureDamageTaken = obj.getDouble("pureDamageTaken");
            result.pureDamageDealt = obj.getDouble("pureDamageDealt");
            result.maxKillstreak = obj.getInt("maxKillstreak");
            result.moneyEarned = obj.getInt("moneyEarned");
            return result;
        }

        BasicDBObject toDBObject(){
            return new BasicDBObject("maxKillStreak", maxKillstreak)
                    .append("kills", kills)
                    .append("deaths", deaths)
                    .append("kdRatio", kdRatio)
                    .append("physicalDamageTaken", physicalDamageTaken)
                    .append("physicalDamageDealt", physicalDamageDealt)
                    .append("magicDamageTaken", magicDamageTaken)
                    .append("magicDamageDealt", magicDamageDealt)
                    .append("pureDamageTaken", pureDamageTaken)
                    .append("pureDamageDealt", pureDamageDealt)
                    .append("moneyEarned", moneyEarned);
        }
    }
}
