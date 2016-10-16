package com.mishkapp.minecraft.plugins.squarekit.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by mishkapp on 16.10.2016.
 */
public class FormatUtils {
    private static NumberFormat round = new DecimalFormat("#0");
    private static NumberFormat tenth = new DecimalFormat("#0.0");
    private static NumberFormat hundredth = new DecimalFormat("#0.00");
    private static NumberFormat thousandth = new DecimalFormat("#0.000");

    public static String round(double d){
        return d > 0 ? "+" + round.format(d) : round.format(d);
    }

    public static String tenth(double d){
        return d > 0 ? "+" + tenth.format(d) : tenth.format(d);
    }

    public static String hundredth(double d){
        return d > 0 ? "+" + hundredth.format(d) : hundredth.format(d);
    }

    public static String thousandth(double d){
        return d > 0 ? "+" + thousandth.format(d) : thousandth.format(d);
    }
}
