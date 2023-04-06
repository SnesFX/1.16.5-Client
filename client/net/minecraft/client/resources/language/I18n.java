/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.language;

import java.util.IllegalFormatException;
import net.minecraft.locale.Language;

public class I18n {
    private static volatile Language language = Language.getInstance();

    static void setLanguage(Language language) {
        I18n.language = language;
    }

    public static String get(String string, Object ... arrobject) {
        String string2 = language.getOrDefault(string);
        try {
            return String.format(string2, arrobject);
        }
        catch (IllegalFormatException illegalFormatException) {
            return "Format error: " + string2;
        }
    }

    public static boolean exists(String string) {
        return language.has(string);
    }
}

