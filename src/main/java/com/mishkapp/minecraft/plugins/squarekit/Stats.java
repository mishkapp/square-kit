package com.mishkapp.minecraft.plugins.squarekit;

/**
 * Created by mishkapp on 28.04.2016.
 */
public class Stats{
    private float manaRegen = 0.0f;
    private double healthRegen = 0.10;
    private float speed = 1.0f;
    private double cooldownRate = 1.0;
    private double physicalResist = 0.0;
    private double magicResist = 0.0;
    private double maxHealth = 100.0;

    public static Stats getMultiplier(){
        Stats result = new Stats();
        result.manaRegen = 1.0f;
        result.healthRegen = 1.0f;
        result.speed = 1.0f;
        result.cooldownRate = 1.0f;
        result.physicalResist = 1.0f;
        result.magicResist = 1.0f;
        result.maxHealth = 1.0;
        return result;
    }

    public static Stats getAddition(){
        Stats result = new Stats();
        result.manaRegen = 0.0f;
        result.healthRegen = 0.0f;
        result.speed = 0.0f;
        result.cooldownRate = 0.0f;
        result.physicalResist = 0.0f;
        result.magicResist = 0.0f;
        result.maxHealth = 0.0;
        return result;
    }

    public float getManaRegen() {
        return manaRegen;
    }

    public double getHealthRegen() {
        return healthRegen;
    }

    public float getSpeed() {
        return speed;
    }

    public double getCooldownRate() {
        return cooldownRate;
    }

    public double getPhysicalResist() {
        return physicalResist;
    }

    public double getMagicResist() {
        return magicResist;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setManaRegen(float manaRegen) {
        this.manaRegen = manaRegen;
    }

    public void setHealthRegen(double healthRegen) {
        this.healthRegen = healthRegen;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setCooldownRate(double cooldownRate) {
        this.cooldownRate = cooldownRate;
    }

    public void setPhysicalResist(double physicalResist) {
        this.physicalResist = physicalResist;
    }

    public void setMagicResist(double magicResist) {
        this.magicResist = magicResist;
    }
}
