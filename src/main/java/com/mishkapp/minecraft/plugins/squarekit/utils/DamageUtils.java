package com.mishkapp.minecraft.plugins.squarekit.utils;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

/**
 * Created by mishkapp on 12.12.2016.
 */
public class DamageUtils {

    public static DamageSource magicDamage(Entity e){
        return EntityDamageSource.builder().entity(e).type(DamageTypes.MAGIC).magical().bypassesArmor().build();
    }

    public static DamageSource pureDamage(Entity e){
        return EntityDamageSource.builder().entity(e).type(DamageTypes.MAGIC).absolute().bypassesArmor().build();
    }

    public static DamageSource physicalDamage(Entity e){
        return EntityDamageSource.builder().entity(e).type(DamageTypes.ATTACK).bypassesArmor().build();
    }
}
