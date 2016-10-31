package com.mishkapp.minecraft.plugins.squarekit.player;

import com.mishkapp.minecraft.plugins.squarekit.KitItem;
import com.mishkapp.minecraft.plugins.squarekit.SquareKit;
import com.mishkapp.minecraft.plugins.squarekit.SuffixFactory;
import com.mishkapp.minecraft.plugins.squarekit.events.KitEvent;
import com.mishkapp.minecraft.plugins.squarekit.suffixes.Suffix;
import com.mishkapp.minecraft.plugins.squarekit.utils.FormatUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static org.spongepowered.api.text.format.TextColors.DARK_BLUE;

/**
 * Created by mishkapp on 27.04.2016.
 */
public class KitPlayer {

    private List<KitItem> kitItems = new ArrayList<>();

    private final double PHYSICAL_DAMAGE = 1.0;     // [1, ∞)
    private final double MAX_MANA = 100.0;          // [0, ∞)
    private final double MANA_REGEN = 0.0;          // (-∞, ∞)
    private final double HEALTH_REGEN = 0.125;      // (-∞, ∞)
    private final double SPEED = 1.0;               // (0, ∞)
    private final double COOLDOWN_RATE = 1.0;       // (0, ∞)
    private final double PHYSICAL_RESIST = 0.0;     // (-∞, 1]
    private final double MAGIC_RESIST = 0.0;        // (-∞, 1]
    private final double MAX_HEALTH = 100.0;        // [1, ∞)
    private final double KNOCKBACK_RESIST = 0.0;    // (-∞, ∞)
    private final double CRITICAL_CHANCE = 0.0;     // [0, 1]
    private final double CRITICAL_POWER = 0.0;      // [0, ∞)
    private final double EVASION = 0.0;             // [0, 1]

    private HashMap<String, HashMap<Suffix, Double>> additions = new HashMap();

    private double currentMana = 0.0;

    private Player player;
    private Scoreboard scoreboard;
    private Objective statsObj;

    //here comes the model
    private UUID uuid;
    private String currentKit = "recruit";
    private int money = 0;
    private int experience = 0;
    private int currentKillstreak;
    private PlayerStats playerStats = new PlayerStats();
    private KitsStats kitsStats = new KitsStats();

    public KitPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        initScoreboard();
        currentMana = getMaxMana();
    }

    private void initScoreboard(){
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

    public double getHealth(){
        return player.health().get();
    }

    public double getAttackDamage(){
        double result = PHYSICAL_DAMAGE;
        for(double i : getPhysicalDamageAdds().values()){
            result += i;
        }
        return Math.max(1, result);
    }

    public float getSpeed(){
        double result = SPEED;
        for(double i : getSpeedAdds().values()){
            result += i;
        }
        return (float)Math.max(Double.MIN_VALUE, result);
    }

    public double getCooldownRate(){
        double result = COOLDOWN_RATE;
        for(double i : getCooldownRateAdds().values()){
            result += i;
        }
        return Math.max(Double.MIN_VALUE, result);
    }

    public double getMaxMana(){
        double result = MAX_MANA;
        for(double i : getMaxManaAdds().values()){
            result += i;
        }
        return Math.max(0, result);
    }

    public double getManaRegen() {
        double result = MANA_REGEN;
        for(double i : getManaRegenAdds().values()){
            result += i;
        }
        return result;
    }

    public double getHealthRegen() {
        double result = HEALTH_REGEN;
        for(double i : getHealthRegenAdds().values()){
            result += i;
        }
        return result;
    }

    public double getPhysicalResist() {
        double result = PHYSICAL_RESIST;
        for(double i : getPhysicalResistAdds().values()){
            result += i;
        }
        return Math.min(1, result);
    }

    public double getMagicResist() {
        double result = MAGIC_RESIST;
        for(double i : getMagicResistAdds().values()){
            result += i;
        }
        return Math.min(1, result);
    }

    public double getMaxHealth() {
        double result = MAX_HEALTH;
        for(double i : getMaxHealthAdds().values()){
            result += i;
        }
        return Math.max(1, result);
    }

    public double getKnockbackResist() {
        double result = KNOCKBACK_RESIST;
        for(double i : getKnockbackResistsAdds().values()){
            result += i;
        }
        return result;
    }

    public double getCriticalChance() {
        double result = CRITICAL_CHANCE;
        for(double i : getCriticalChanceAdds().values()){
            result += i;
        }
        if(result > 0){
            return Math.min(1, result);
        } else {
            return Math.max(0, result);
        }
    }

    public double getCriticalPower() {
        double result = CRITICAL_POWER;
        for(double i : getCriticalPowerAdds().values()){
            result += i;
        }
        return Math.max(0, result);
    }

    public double getEvasion() {
        double result = EVASION;
        for(double i : getEvasionAdds().values()){
            result += i;
        }
        if(result > 0){
            return Math.min(1, result);
        } else {
            return Math.max(0, result);
        }
    }

    public HashMap<Suffix, Double> getAdditions(String s){
        if(!additions.containsKey(s)){
            additions.put(s, new HashMap<>());
        }
        return additions.get(s);
    }

    public HashMap<Suffix, Double> getPhysicalDamageAdds() {
        return getAdditions("physical-damage");
    }

    public HashMap<Suffix, Double> getMaxManaAdds() {
        return getAdditions("max-mana");
    }

    public HashMap<Suffix, Double> getManaRegenAdds() {
        return getAdditions("mana-regen");
    }

    public HashMap<Suffix, Double> getHealthRegenAdds() {
        return getAdditions("health-regen");
    }

    public HashMap<Suffix, Double> getSpeedAdds() {
        return getAdditions("speed");
    }

    public HashMap<Suffix, Double> getPhysicalResistAdds() {
        return getAdditions("physical-resist");
    }

    public HashMap<Suffix, Double> getMagicResistAdds() {
        return getAdditions("magic-resist");
    }

    public HashMap<Suffix, Double> getCooldownRateAdds() {
        return getAdditions("cooldown-rate");
    }

    public HashMap<Suffix, Double> getMaxHealthAdds() {
        return getAdditions("max-health");
    }

    public HashMap<Suffix, Double> getKnockbackResistsAdds() {
        return getAdditions("knockback-resistance");
    }

    public HashMap<Suffix, Double> getCriticalChanceAdds() {
        return getAdditions("critical-chance");
    }

    public HashMap<Suffix, Double> getCriticalPowerAdds() {
        return getAdditions("critical-power");
    }

    public HashMap<Suffix, Double> getEvasionAdds() {
        return getAdditions("evasion");
    }


    public double getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
    }

    public String getCurrentKit() {
        return currentKit;
    }

    public void setCurrentKit(String currentKit) {
        this.currentKit = currentKit;
    }

    public double addPhysicalDamage(double damage){
        damage = addDamage(damage * (1.0 - getPhysicalResist()));
        return damage;
    }

    public double addMagicDamage(double damage){
        damage = addDamage(damage * (1.0 - getMagicResist()));
        return damage;
    }

    public double addPureDamage(double damage){
        damage = addDamage(damage);
        return damage;
    }

    private double addDamage(double damage){
        double health = player.get(Keys.HEALTH).get();
        double newHealth = health - damage;
        if(newHealth < 0) {
            newHealth = 0;
            damage = health;
        }
        player.offer(Keys.HEALTH, newHealth);
        return damage;
    }

    public void tick(){
        if(getMcPlayer().isOnline()){
            tickRegens();
            updateScoreboard();
        } else {
            SquareKit.getPlayersRegistry().unregisterPlayer(getMcPlayer());
        }
    }

    public void onKill(){
        playerStats.setKills(playerStats.getKills() + 1);
        KitsStats.KitStats st = kitsStats.get(currentKit);
        st.setKills(st.getKills() + 1);
        currentKillstreak += 1;
        recalculateKDRatio();
    }

    public void onDeath(){
        playerStats.setDeaths(playerStats.getDeaths() + 1);
        KitsStats.KitStats st = kitsStats.get(currentKit);
        st.setDeaths(st.getDeaths() + 1);
        resetKillstreak();
        recalculateKDRatio();
    }

    private void resetKillstreak(){
        if(playerStats.getMaxKillstreak() < currentKillstreak){
            playerStats.setMaxKillstreak(currentKillstreak);
        }
        KitsStats.KitStats st = kitsStats.get(currentKit);
        if(st.getMaxKillstreak() < currentKillstreak){
            st.setMaxKillstreak(currentKillstreak);
        }
        currentKillstreak = 0;
    }

    private void recalculateKDRatio(){
        double deaths = playerStats.getDeaths();
        if(deaths == 0){
            deaths = 1;
        }
        playerStats.setKdRatio((double)playerStats.getKills() / deaths);
        KitsStats.KitStats st = kitsStats.get(currentKit);
        deaths = st.getDeaths();
        if(deaths == 0){
            deaths = 1;
        }
        st.setKdRatio((double)st.getKills() / deaths);
    }

    public void update(){
        System.out.println("UPDATE");

        List<KitItem> items = SuffixFactory.getKitItems(this);

        List<KitItem> newItems = new ArrayList<>();
        List<KitItem> oldItems = new ArrayList<>();
        newItems.addAll(items);
        oldItems.addAll(kitItems);

        kitItems.iterator().forEachRemaining(o -> {
            if(newItems.contains(o)){
                newItems.remove(o);
                oldItems.remove(o);
            }
        });

        newItems.forEach(
                o -> o.getSuffices().forEach(Suffix::register)
        );
        oldItems.forEach(
                o -> o.getSuffices().forEach(Suffix::unregister)
        );

        kitItems.removeAll(oldItems);
        kitItems.addAll(newItems);

        updateStats();
    }

    public void forceUpdate(){
        System.out.println("FORCE UPDATE");
        purgeAdditions();
        kitItems = SuffixFactory.getKitItems(this);
        kitItems.forEach(
                o -> o.getSuffices().forEach(Suffix::register)
        );
        updateStats();
    }

    public void unregister(Suffix s) {
        additions.values().forEach(
                o -> {
                    Iterator<Map.Entry<Suffix,Double>> it = o.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Suffix,Double> k = it.next();
                        //I use '==' instead of 'equals' because we need to find SAME object
                        if (k.getKey() == s) {
                            it.remove();
                        }
                    }
                });
    }

    private void purgeAdditions() {
        additions.values().forEach(HashMap::clear);
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
        return Text.builder("Мана: " + FormatUtils.unsignedRound(currentMana) + "/" + FormatUtils.unsignedRound(getMaxMana()))
                .color(DARK_BLUE)
                .build();
    }

    private Text getPhysicalResistText(){
        return Text.of("Ф.Сопр: " + FormatUtils.unsignedTenth(getPhysicalResist() * 100) + "%");
    }

    private Text getMagicResistText(){
        return Text.of("М.Сопр: " + FormatUtils.unsignedTenth(getMagicResist() * 100) + "%");
    }


    public void updateStats(){
        player.offer(Keys.MAX_HEALTH, getMaxHealth());
        player.offer(Keys.WALKING_SPEED, (getSpeed() * 0.1));
        player.offer(Keys.ATTACK_DAMAGE, getAttackDamage());
    }

    public void handleEvent(KitEvent event){
        kitItems.forEach(
                o -> o.getSuffices().forEach(
                        s -> s.handle(event)
                )
        );
    }

    public List<KitItem> getKitItems() {
        return kitItems;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KitPlayer)) return false;

        KitPlayer kitPlayer = (KitPlayer) o;

        return player.equals(kitPlayer.player);

    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    public static KitPlayer getKitPlayer(MongoDatabase mongoDatabase, Player player){
        MongoCollection collection = mongoDatabase.getCollection("players");
        UUID uuid = player.getUniqueId();
        Document document = (Document) collection.find(eq("uuid", uuid.toString())).first();
        KitPlayer result = new KitPlayer(player);
        if(document != null){
            result.fromDocument(document);
        }
        return result;
    }

    public void saveKitPlayer(MongoDatabase mongoDatabase){
        MongoCollection collection = mongoDatabase.getCollection("players");
        Document document = (Document) collection.find(eq("uuid", uuid.toString())).first();
        if(document != null){
            collection.findOneAndReplace(eq("uuid", uuid.toString()), toDocument());
        } else {
            collection.insertOne(toDocument());
        }
    }

    private void fromDocument(Document document){
        currentKit = document.getString("currentKit");
        money = document.getInteger("money");
        experience = document.getInteger("experience");
        currentKillstreak = document.getInteger("currentKillstreak");
        playerStats = PlayerStats.fromDocument(document.get("playerStats", Document.class));
        kitsStats = KitsStats.fromDocument(document.get("kitsStats", Document.class));
    }

    private Document toDocument(){
        return new Document("uuid", uuid.toString())
                .append("currentKit", currentKit)
                .append("money", money)
                .append("experience", experience)
                .append("currentKillstreak", currentKillstreak)
                .append("playerStats", playerStats.toDocument())
                .append("kitsStats", kitsStats.toDocument());
    }
}
