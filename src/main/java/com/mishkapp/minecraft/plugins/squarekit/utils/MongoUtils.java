package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.flowpowered.math.vector.Vector3d;
import org.bson.Document;

/**
 * Created by mishkapp on 03.12.2016.
 */
public class MongoUtils {

    public static Document vectorToDocument(Vector3d vector){
        return new Document()
                .append("x", vector.getX())
                .append("y", vector.getY())
                .append("z", vector.getZ());
    }

    public static Vector3d vectorFromDocument(Document document){
        return new Vector3d(
                document.getDouble("x"),
                document.getDouble("y"),
                document.getDouble("z")
                );
    }
}
