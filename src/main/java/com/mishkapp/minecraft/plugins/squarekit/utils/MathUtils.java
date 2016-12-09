package com.mishkapp.minecraft.plugins.squarekit.utils;

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
}
