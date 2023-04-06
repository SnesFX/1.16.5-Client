/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public enum ChatFormatting {
    BLACK("BLACK", '0', 0, 0),
    DARK_BLUE("DARK_BLUE", '1', 1, 170),
    DARK_GREEN("DARK_GREEN", '2', 2, 43520),
    DARK_AQUA("DARK_AQUA", '3', 3, 43690),
    DARK_RED("DARK_RED", '4', 4, 11141120),
    DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
    GOLD("GOLD", '6', 6, 16755200),
    GRAY("GRAY", '7', 7, 11184810),
    DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
    BLUE("BLUE", '9', 9, 5592575),
    GREEN("GREEN", 'a', 10, 5635925),
    AQUA("AQUA", 'b', 11, 5636095),
    RED("RED", 'c', 12, 16733525),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
    YELLOW("YELLOW", 'e', 14, 16777045),
    WHITE("WHITE", 'f', 15, 16777215),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1, null);
    
    private static final Map<String, ChatFormatting> FORMATTING_BY_NAME;
    private static final Pattern STRIP_FORMATTING_PATTERN;
    private final String name;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    private final int id;
    @Nullable
    private final Integer color;

    private static String cleanName(String string) {
        return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private ChatFormatting(String string2, char c, int n2, @Nullable Integer n3) {
        this(string2, c, false, n2, n3);
    }

    private ChatFormatting(String string2, char c, boolean bl) {
        this(string2, c, bl, -1, null);
    }

    private ChatFormatting(String string2, char c, boolean bl, int n2, @Nullable Integer n3) {
        this.name = string2;
        this.code = c;
        this.isFormat = bl;
        this.id = n2;
        this.color = n3;
        this.toString = "\u00a7" + c;
    }

    public int getId() {
        return this.id;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    @Nullable
    public Integer getColor() {
        return this.color;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.toString;
    }

    @Nullable
    public static String stripFormatting(@Nullable String string) {
        return string == null ? null : STRIP_FORMATTING_PATTERN.matcher(string).replaceAll("");
    }

    @Nullable
    public static ChatFormatting getByName(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return FORMATTING_BY_NAME.get(ChatFormatting.cleanName(string));
    }

    @Nullable
    public static ChatFormatting getById(int n) {
        if (n < 0) {
            return RESET;
        }
        for (ChatFormatting chatFormatting : ChatFormatting.values()) {
            if (chatFormatting.getId() != n) continue;
            return chatFormatting;
        }
        return null;
    }

    @Nullable
    public static ChatFormatting getByCode(char c) {
        char c2 = Character.toString(c).toLowerCase(Locale.ROOT).charAt(0);
        for (ChatFormatting chatFormatting : ChatFormatting.values()) {
            if (chatFormatting.code != c2) continue;
            return chatFormatting;
        }
        return null;
    }

    public static Collection<String> getNames(boolean bl, boolean bl2) {
        ArrayList arrayList = Lists.newArrayList();
        for (ChatFormatting chatFormatting : ChatFormatting.values()) {
            if (chatFormatting.isColor() && !bl || chatFormatting.isFormat() && !bl2) continue;
            arrayList.add(chatFormatting.getName());
        }
        return arrayList;
    }

    static {
        FORMATTING_BY_NAME = Arrays.stream(ChatFormatting.values()).collect(Collectors.toMap(chatFormatting -> ChatFormatting.cleanName(chatFormatting.name), chatFormatting -> chatFormatting));
        STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

