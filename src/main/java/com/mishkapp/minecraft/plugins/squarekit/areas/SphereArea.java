package com.mishkapp.minecraft.plugins.squarekit.areas;

import com.flowpowered.math.vector.Vector3d;
import com.mishkapp.minecraft.plugins.squarekit.utils.MongoUtils;
import org.bson.Document;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class SphereArea extends Area {

    protected Vector3d center;
    protected Vector3d fi;

    public SphereArea(String id, String worldName, Vector3d center, Vector3d fi){
        this.id = id;
        world = Sponge.getServer().getWorld(worldName).orElse(null);
        this.center = center;
        this.fi = fi;
    }

    public SphereArea(String id, String worldName, double x1, double y1, double z1, double x2, double y2, double z2){
        this.id = id;
        world = Sponge.getServer().getWorld(worldName).orElse(null);
        center = new Vector3d(x1, y1, z1);
        fi = new Vector3d(x2, y2, z2);
    }

    @Override
    public Vector3d getCenter() {
        return center;
    }

    public Vector3d getFi() {
        return fi;
    }

    @Override
    public boolean isInside(Location l) {
        if(!(l.getExtent().equals(world))){
            return false;
        }

        double xDelta = Math.abs(center.getX() - l.getX());
        double yDelta = Math.abs(center.getY() - l.getY());
        double zDelta = Math.abs(center.getZ() - l.getZ());

        return xDelta <= fi.getX() && yDelta <= fi.getY() && zDelta <= fi.getZ();
    }

    @Override
    public void fillBoundPoints() {
        boundPoints = new ArrayList<>();
        double minX = center.getX() - fi.getX();
        double minY = center.getY() - fi.getY();
        double minZ = center.getZ() - fi.getZ();

        double maxX = center.getX() + fi.getX();
        double maxY = center.getY() + fi.getY();
        double maxZ = center.getZ() + fi.getZ();

        double epsX = (Math.PI) / (maxX - minX);
        epsX /= 3.0;
        double epsY = (Math.PI * 2.0) / (maxY - minY);
        epsY /= 3.0;

            for (double i = 0; i <= Math.PI; i += epsX){
                for (double j = 0; j <= Math.PI * 2; j += epsY){
                    boundPoints.add(new Vector3d(
                            center.getX() + fi.getX() * Math.sin(i) * Math.cos(j),
                            center.getY() + fi.getY() * Math.sin(i) * Math.sin(j),
                            center.getZ() + fi.getZ() * Math.cos(i)
                    ));
                }
            }
    }

    @Override
    protected Document definitionToDocument() {
        return new Document()
                .append("type", "sphere")
                .append("world", world.getName())
                .append("center", MongoUtils.vectorToDocument(center))
                .append("fi", MongoUtils.vectorToDocument(fi));
    }
}
