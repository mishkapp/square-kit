package com.mishkapp.minecraft.plugins.squarekit.player;

import com.mongodb.BasicDBObject;

/**
 * Created by mishkapp on 29.10.2016.
 */
public class PlayerStats {
    private int maxKillstreak = 0;
    private int kills = 0;
    private int deaths = 0;
    private double kdRatio = 0;
    private double physicalDamageTaken = 0;
    private double physicalDamageDealt = 0;
    private double magicDamageTaken = 0;
    private double magicDamageDealt = 0;
    private double pureDamageTaken = 0;
    private double pureDamageDealt = 0;
    private int moneyEarned = 0;
    private int moneySpent = 0;

    static PlayerStats fromDBObject(BasicDBObject obj){
        PlayerStats result = new PlayerStats();
        result.kills = obj.getInt("kills");
        result.deaths = obj.getInt("deaths");
        result.kdRatio = obj.getDouble("kdRatio");
        result.physicalDamageTaken = obj.getDouble("physicalDamageTaken");
        result.physicalDamageDealt = obj.getDouble("physicalDamageDealt");
        result.magicDamageTaken = obj.getDouble("magicDamageTaken");
        result.magicDamageDealt = obj.getDouble("magicDamageDealt");
        result.pureDamageTaken = obj.getDouble("pureDamageTaken");
        result.pureDamageDealt = obj.getDouble("pureDamageDealt");
        result.moneyEarned = obj.getInt("moneyEarned");
        result.moneySpent = obj.getInt("moneySpent");
        result.maxKillstreak = obj.getInt("maxKillstreak");
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
                .append("moneyEarned", moneyEarned)
                .append("moneySpent", moneySpent);
    }
}
