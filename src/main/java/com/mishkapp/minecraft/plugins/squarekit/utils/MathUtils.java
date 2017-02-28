package com.mishkapp.minecraft.plugins.squarekit.utils;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishkapp on 09.12.2016.
 */
public class MathUtils {

    //phi - azimuth, theta - inclination, delta - max difference between angles
    public static boolean isSameDirection(double phi1, double phi2, double theta1, double theta2, double delta){
        if(Math.abs(theta1 - theta2) > delta){
            return false;
        }

        if(Math.abs(phi1 - phi2) <= delta) {
            return true;
        }

        if(Math.abs(phi1 - phi2) >= (360.0 - delta)){
            return true;
        }

        return false;
    }

    public static Vector3d rotatePoint(Vector3d point, Vector3d oPos, Vector3d rot){
        Quaterniond xQ = Quaterniond.fromAngleDegAxis(rot.getX(), new Vector3d(1, 0, 0));
        Quaterniond yQ = Quaterniond.fromAngleDegAxis(rot.getY(), new Vector3d(0, 1, 0));
        Quaterniond zQ = Quaterniond.fromAngleDegAxis(rot.getZ(), new Vector3d(0, 0, 1));

        point = point.sub(oPos);
        point = zQ.rotate(yQ.rotate(xQ.rotate(point)));
        point = point.add(oPos);

        return point;
    }

    public static List<Vector3d> rotatePoints(List<Vector3d> points, Vector3d oPos, Vector3d rot){
        List<Vector3d> result = new ArrayList<>();
        points.forEach(p -> result.add(rotatePoint(p, oPos, rot)));
        return result;
    }

    public static Vector3d lookToRot(Vector3d look){
        return new Vector3d(
                look.getX(),
                look.getY() * -1,
                look.getZ()
        );
    }
}
