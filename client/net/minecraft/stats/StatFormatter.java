/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.Util;

public interface StatFormatter {
    public static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("########0.00"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    public static final StatFormatter DEFAULT = NumberFormat.getIntegerInstance(Locale.US)::format;
    public static final StatFormatter DIVIDE_BY_TEN = n -> DECIMAL_FORMAT.format((double)n * 0.1);
    public static final StatFormatter DISTANCE = n -> {
        double d = (double)n / 100.0;
        double d2 = d / 1000.0;
        if (d2 > 0.5) {
            return DECIMAL_FORMAT.format(d2) + " km";
        }
        if (d > 0.5) {
            return DECIMAL_FORMAT.format(d) + " m";
        }
        return n + " cm";
    };
    public static final StatFormatter TIME = n -> {
        double d = (double)n / 20.0;
        double d2 = d / 60.0;
        double d3 = d2 / 60.0;
        double d4 = d3 / 24.0;
        double d5 = d4 / 365.0;
        if (d5 > 0.5) {
            return DECIMAL_FORMAT.format(d5) + " y";
        }
        if (d4 > 0.5) {
            return DECIMAL_FORMAT.format(d4) + " d";
        }
        if (d3 > 0.5) {
            return DECIMAL_FORMAT.format(d3) + " h";
        }
        if (d2 > 0.5) {
            return DECIMAL_FORMAT.format(d2) + " m";
        }
        return d + " s";
    };

    public String format(int var1);
}

