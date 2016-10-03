package com.mishkapp.minecraft.plugins.squarekit;

import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class KitPlayer {

    private List<? extends Suffix> suffixes = new ArrayList<>();

    private final double ATTACK_DAMAGE = 1.0;
    private final double MAX_MANA = 10.0;
    private final double MANA_REGEN = 0.0;
    private final double HEALTH_REGEN = 0.10;
    private final float SPEED = 1.0f;
    private final double COOLDOWN_RATE = 1.0;
    private final double PHYSICAL_RESIST = 0.0;
    private final double MAGIC_RESIST = 0.0;
    private final double MAX_HEALTH = 100.0;

    private HashMap<Suffix, Double> attackDamageAdds = new HashMap<>();
    private HashMap<Suffix, Double> maxManaAds = new HashMap<>();
    private HashMap<Suffix, Double> manaRegenAdds = new HashMap<>();
    private HashMap<Suffix, Double> healthRegenAdds = new HashMap<>();
    private HashMap<Suffix, Float> speedAdds = new HashMap<>();
    private HashMap<Suffix, Double> physicalResistAdds = new HashMap<>();
    private HashMap<Suffix, Double> magicResistAdds = new HashMap<>();
    private HashMap<Suffix, Double> cooldownRateAdds = new HashMap<>();
    private HashMap<Suffix, Double> maxHealthAdds = new HashMap<>();

    private double currentMana = 0.0;

    private Player player;
    private Scoreboard scoreboard;
    private Objective statsObj;

    //Stats
    private Stats base = new Stats();


    public KitPlayer(Player player) {
        this.player = player;

        scoreboard = Scoreboard.builder()
                .build();

        statsObj = Objective.builder()
                .name("stats")
                .criterion(Criteria.DUMMY)
                .displayName(Text.of("Stats"))
                .build();
        scoreboard.addObjective(statsObj);
        updateScoreboard();
        player.setScoreboard(scoreboard);
    }

    public UUID getUuid(){
        return player.getUniqueId();
    }

    public double getAttackDamage(){
        double result = ATTACK_DAMAGE;
        for(double i : attackDamageAdds.values()){
            result += i;
        }
        return result;
    }

    public float getSpeed(){
        float result = SPEED;
        for(float i : speedAdds.values()){
            result += i;
        }
        return result;
    }

    public double getCooldownRate(){
        double result = COOLDOWN_RATE;
        for(double i : cooldownRateAdds.values()){
            result += i;
        }
        return result;
    }

    public double getMaxMana(){
        double result = MAX_MANA;
        for(double i : maxManaAds.values()){
            result += i;
        }
        return result;
    }

    public double getManaRegen() {
        double result = MANA_REGEN;
        for(double i : manaRegenAdds.values()){
            result += i;
        }
        return result;
    }

    public double getHealthRegen() {
        double result = HEALTH_REGEN;
        for(double i : healthRegenAdds.values()){
            result += i;
        }
        return result;
    }

    public double getPhysicalResist() {
        double result = PHYSICAL_RESIST;
        for(double i : physicalResistAdds.values()){
            result += i;
        }
        return result;
    }

    public double getMagicResist() {
        double result = MAGIC_RESIST;
        for(double i : magicResistAdds.values()){
            result += i;
        }
        return result;
    }

    public double getMaxHealth() {
        double result = MAX_HEALTH;
        for(double i : maxHealthAdds.values()){
            result += i;
        }
        return result;
    }

    public HashMap<Suffix, Double> getAttackDamageAdds() {
        return attackDamageAdds;
    }

    public HashMap<Suffix, Double> getMaxManaAds() {
        return maxManaAds;
    }

    public HashMap<Suffix, Double> getManaRegenAdds() {
        return manaRegenAdds;
    }

    public HashMap<Suffix, Double> getHealthRegenAdds() {
        return healthRegenAdds;
    }

    public HashMap<Suffix, Float> getSpeedAdds() {
        return speedAdds;
    }

    public HashMap<Suffix, Double> getPhysicalResistAdds() {
        return physicalResistAdds;
    }

    public HashMap<Suffix, Double> getMagicResistAdds() {
        return magicResistAdds;
    }

    public HashMap<Suffix, Double> getCooldownRateAdds() {
        return cooldownRateAdds;
    }

    public HashMap<Suffix, Double> getMaxHealthAdds() {
        return maxHealthAdds;
    }

    public double getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
    }

    public void addPhysicalDamage(double damage){
        addPureDamage(damage * (1.0 - getPhysicalResist()));
    }

    public void addMagicDamage(double damage){
        addPureDamage(damage * (1.0 - getMagicResist()));
    }

    public void addPureDamage(double damage){
        double health = player.get(Keys.HEALTH).get();
        double newHealth = health - damage;
        if(newHealth < 0) {newHealth = 0;}
        player.offer(Keys.HEALTH, newHealth);
    }

    public void tick(){
        if(getMcPlayer().isOnline()){
            tickRegens();
            updateScoreboard();
        } else {
            SquareKit.getPlayersRegistry().unregisterPlayer(getMcPlayer());
        }
    }

    public void update(){
        System.out.println("UPDATE");
        purgeAdditions();
        suffixes = SuffixFactory.getSuffixes(player);
        suffixes.forEach(Suffix::register);
        updateStats();
    }

    private void purgeAdditions() {
        attackDamageAdds = new HashMap<>();
        manaRegenAdds = new HashMap<>();
        healthRegenAdds = new HashMap<>();
        speedAdds = new HashMap<>();
        physicalResistAdds = new HashMap<>();
        magicResistAdds = new HashMap<>();
        cooldownRateAdds = new HashMap<>();
        maxHealthAdds = new HashMap<>();
        maxManaAds = new HashMap<>();
    }

    private void updateScoreboard(){
        for(Text t : statsObj.getScores().keySet()){
            statsObj.removeScore(t);
        }

        statsObj.getOrCreateScore(getPhysicalResistText()).setScore(2);
        statsObj.getOrCreateScore(getMagicResistText()).setScore(1);
        statsObj.getOrCreateScore(getManaScoreText()).setScore(0);

        scoreboard.updateDisplaySlot(statsObj, DisplaySlots.SIDEBAR);
    }

    private Text getManaScoreText() {
        return Text.of("Мана: " + Formatters.round.format(currentMana) + "/" + Formatters.round.format(getMaxMana()));
    }

    private Text getPhysicalResistText(){
        return Text.of("P.Res: " + Formatters.tenth.format(getPhysicalResist() * 100) + "%");
    }

    private Text getMagicResistText(){
        return Text.of("M.Res: " + Formatters.tenth.format(getMagicResist() * 100) + "%");
    }


    public void updateStats(){
        player.offer(Keys.MAX_HEALTH, getMaxHealth());
        player.offer(Keys.WALKING_SPEED, (getSpeed() * 0.2));
        player.offer(Keys.ATTACK_DAMAGE, getAttackDamage());
    }

    public void handleEvent(KitEvent event){
        suffixes.forEach(s -> s.handle(event));
    }

    public List<? extends Suffix> getSuffixes() {
        return suffixes;
    }

    private void tickRegens(){
        regenHealth();
        regenMana();
    }

    private void regenMana(){
        double maxMana = getMaxMana();
        double manaDelta = maxMana - currentMana;
        double manaRegen = getManaRegen();
        if(maxMana == currentMana){
            return;
        }
        if(manaDelta < manaRegen){
            currentMana += manaDelta;
        } else {
            currentMana += manaRegen;
        }
    }

    private void regenHealth(){
        HealthData hd = player.getHealthData();
        double health = hd.health().get();
        if(health <= 0 || health == getMaxHealth()) {return;}
        double newHealth = health + getHealthRegen();
        if(newHealth > hd.maxHealth().get()) {newHealth = hd.maxHealth().get().intValue();}
        player.offer(Keys.HEALTH, newHealth);
    }

    public Player getMcPlayer() {
        return player;
    }
}

