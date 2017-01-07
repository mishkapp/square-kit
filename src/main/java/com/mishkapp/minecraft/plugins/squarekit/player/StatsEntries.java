package com.mishkapp.minecraft.plugins.squarekit.player;

import com.mishkapp.minecraft.plugins.squarekit.player.statsentries.*;
import org.spongepowered.api.text.Text;

import java.util.HashMap;

/**
 * Created by mishkapp on 05.01.2017.
 */
public class StatsEntries {

    private static HashMap<String, StatsEntry> entries;

    static {
        init();
    }

    public static void init(){
        entries = new HashMap<>();
        entries.put("attack", new AttackDamageEntry());
        entries.put("money", new MoneyEntry());
        entries.put("exp", new ExpEntry());
        entries.put("level", new LevelEntry());
        entries.put("mana", new ManaEntry());
        entries.put("health", new HealthEntry());
        entries.put("magic-resist", new MagicResistEntry());
        entries.put("physical-resist", new PhysicalResistEntry());
        entries.put("streak", new StreakEntry());
        entries.put("kit", new KitEntry());
        entries.put("mana-regen", new ManaRegenEntry());
        entries.put("health-regen", new HealthRegenEntry());
        entries.put("speed", new SpeedEntry());
        entries.put("cooldown-rate", new CooldownRateEntry());
        entries.put("critical-chance", new CriticalChanceEntry());
        entries.put("critical-power", new CriticalPowerEntry());
        entries.put("evasion", new EvasionEntry());
        entries.put("money-multiplier", new MoneyMultiplierEntry());
        entries.put("kdr", new KdRatioEntry());
    }

    public static Text getText(String key, KitPlayer kitPlayer){
        if(entries.containsKey(key)){
            return entries.get(key).getText(kitPlayer);
        } else {
            return Text.of(key);
        }
    }

    public static String[] getRegisteredEntries(){
        return entries.keySet().toArray(new String[entries.size()]);
    }

    public static Text getDescription(String key){
        if(entries.containsKey(key)){
            return entries.get(key).getDescription();
        } else {
            return Text.of(key);
        }
    }

    public static String getRawDescription(String key){
        if(entries.containsKey(key)){
            return entries.get(key).getRawDescription();
        } else {
            return key;
        }
    }
}
