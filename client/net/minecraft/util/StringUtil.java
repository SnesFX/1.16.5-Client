/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String formatTickDuration(int n) {
        int n2 = n / 20;
        int n3 = n2 / 60;
        if ((n2 %= 60) < 10) {
            return n3 + ":0" + n2;
        }
        return n3 + ":" + n2;
    }

    public static String stripColor(String string) {
        return STRIP_COLOR_PATTERN.matcher(string).replaceAll("");
    }

    public static boolean isNullOrEmpty(@Nullable String string) {
        return StringUtils.isEmpty((CharSequence)string);
    }
}

