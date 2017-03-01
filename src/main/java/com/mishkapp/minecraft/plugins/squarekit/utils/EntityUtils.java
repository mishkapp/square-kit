package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

/**
 * Created by mishkapp on 01.03.17.
 */
public class EntityUtils {

    public static double getBlockRayHitDistance(Entity e, double distance){
        Vector3d entityPos = e.getLocation().getPosition();
        return entityPos.distance(getBlockRayHitPoint(e, distance));
    }

    public static Vector3d getBlockRayHitPoint(Entity e, double distance){
        BlockRay<World> blockRay = BlockRay.from(e)
                .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                //due to it block nature...
                .distanceLimit(distance + 1)
                .build();

        BlockRayHit<World> rayHit = blockRay.end().get();
        return rayHit.getPosition();
    }
}
