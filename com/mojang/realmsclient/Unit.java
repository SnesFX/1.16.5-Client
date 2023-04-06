/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient;

import java.util.Locale;

public enum Unit {
    B,
    KB,
    MB,
    GB;
    

    public static Unit getLargest(long l) {
        if (l < 1024L) {
            return B;
        }
        try {
            int n = (int)(Math.log(l) / Math.log(1024.0));
            String string = String.valueOf("KMGTPE".charAt(n - 1));
            return Unit.valueOf(string + "B");
        }
        catch (Exception exception) {
            return GB;
        }
    }

    public static double convertTo(long l, Unit unit) {
        if (unit == B) {
            return l;
        }
        return (double)l / Math.pow(1024.0, unit.ordinal());
    }

    public static String humanReadable(long l) {
        int n = 1024;
        if (l < 1024L) {
            return l + " B";
        }
        int n2 = (int)(Math.log(l) / Math.log(1024.0));
        String string = "KMGTPE".charAt(n2 - 1) + "";
        return String.format(Locale.ROOT, "%.1f %sB", (double)l / Math.pow(1024.0, n2), string);
    }

    public static String humanReadable(long l, Unit unit) {
        return String.format("%." + (unit == GB ? "1" : "0") + "f %s", Unit.convertTo(l, unit), unit.name());
    }
}

