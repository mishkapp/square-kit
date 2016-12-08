package com.mishkapp.minecraft.plugins.squarekit.areas;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.utils.MongoUtils;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class CuboidArea extends Area {

    protected AABB aabb;

    public CuboidArea(String id, String worldName, Vector3d min, Vector3d max){
        this.id = id;
        world = Sponge.getServer().getWorld(worldName).orElse(null);
        aabb = new AABB(min, max);
    }

    public CuboidArea(String id, String worldName, double x1, double y1, double z1, double x2, double y2, double z2){
        this.id = id;
        world = Sponge.getServer().getWorld(worldName).orElse(null);
        aabb = new AABB(x1, y1, z1, x2, y2, z2);
    }

    public AABB getAabb() {
        return aabb;
    }

    @Override
    public boolean isInside(Location l) {
        return l.getExtent().equals(world) &&
                aabb.contains(l.getBlockPosition());
    }

    @Override
    public Vector3d getCenter() {
        return aabb.getCenter();
    }

    @Override
    public void fillBoundPoints() {
        boundPoints = new ArrayList<>();
        double minX = aabb.getMin().getX();
        double minY = aabb.getMin().getY();
        double minZ = aabb.getMin().getZ();

        double maxX = aabb.getMax().getX();
        double maxY = aabb.getMax().getY();
        double maxZ = aabb.getMax().getZ();

        for (double d = minX; d < maxX; d += 0.5){
            for (double c = minY; c < maxY; c += 0.5){
                boundPoints.add(new Vector3d(d, c, minZ));
                boundPoints.add(new Vector3d(d, c, maxZ));
            }
        }

        for (double d = minY; d < maxY; d += 0.5){
            for (double c = minZ; c < maxZ; c += 0.5){
                boundPoints.add(new Vector3d(minX, d, c));
                boundPoints.add(new Vector3d(maxX, d, c));
            }
        }

        for (double d = minZ; d < maxZ; d += 0.5){
            for (double c = minX; c < maxX; c += 0.5) {
                boundPoints.add(new Vector3d(c, minY, d));
                boundPoints.add(new Vector3d(c, maxY, d));
            }

        }
    }

    @Override
    protected Document definitionToDocument() {
        return new Document()
                .append("type", "cuboid")
                .append("world", world.getName())
                .append("min", MongoUtils.vectorToDocument(aabb.getMin()))
                .append("max", MongoUtils.vectorToDocument(aabb.getMax()));
    }
}
