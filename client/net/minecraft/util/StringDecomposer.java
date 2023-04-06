/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Unit;

public class StringDecomposer {
    private static final Optional<Object> STOP_ITERATION = Optional.of(Unit.INSTANCE);

    private static boolean feedChar(Style style, FormattedCharSink formattedCharSink, int n, char c) {
        if (Character.isSurrogate(c)) {
            return formattedCharSink.accept(n, style, 65533);
        }
        return formattedCharSink.accept(n, style, c);
    }

    public static boolean iterate(String string, Style style, FormattedCharSink formattedCharSink) {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (i + 1 >= n) {
                    if (formattedCharSink.accept(i, style, 65533)) break;
                    return false;
                }
                char c2 = string.charAt(i + 1);
                if (Character.isLowSurrogate(c2)) {
                    if (!formattedCharSink.accept(i, style, Character.toCodePoint(c, c2))) {
                        return false;
                    }
                    ++i;
                    continue;
                }
                if (formattedCharSink.accept(i, style, 65533)) continue;
                return false;
            }
            if (StringDecomposer.feedChar(style, formattedCharSink, i, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean iterateBackwards(String string, Style style, FormattedCharSink formattedCharSink) {
        int n = string.length();
        for (int i = n - 1; i >= 0; --i) {
            char c = string.charAt(i);
            if (Character.isLowSurrogate(c)) {
                if (i - 1 < 0) {
                    if (formattedCharSink.accept(0, style, 65533)) break;
                    return false;
                }
                char c2 = string.charAt(i - 1);
                if (!(Character.isHighSurrogate(c2) ? !formattedCharSink.accept(--i, style, Character.toCodePoint(c2, c)) : !formattedCharSink.accept(i, style, 65533))) continue;
                return false;
            }
            if (StringDecomposer.feedChar(style, formattedCharSink, i, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean iterateFormatted(String string, Style style, FormattedCharSink formattedCharSink) {
        return StringDecomposer.iterateFormatted(string, 0, style, formattedCharSink);
    }

    public static boolean iterateFormatted(String string, int n, Style style, FormattedCharSink formattedCharSink) {
        return StringDecomposer.iterateFormatted(string, n, style, style, formattedCharSink);
    }

    public static boolean iterateFormatted(String string, int n, Style style, Style style2, FormattedCharSink formattedCharSink) {
        int n2 = string.length();
        Style style3 = style;
        for (int i = n; i < n2; ++i) {
            char c;
            char c2 = string.charAt(i);
            if (c2 == '\u00a7') {
                if (i + 1 >= n2) break;
                c = string.charAt(i + 1);
                ChatFormatting chatFormatting = ChatFormatting.getByCode(c);
                if (chatFormatting != null) {
                    style3 = chatFormatting == ChatFormatting.RESET ? style2 : style3.applyLegacyFormat(chatFormatting);
                }
                ++i;
                continue;
            }
            if (Character.isHighSurrogate(c2)) {
                if (i + 1 >= n2) {
                    if (formattedCharSink.accept(i, style3, 65533)) break;
                    return false;
                }
                c = string.charAt(i + 1);
                if (Character.isLowSurrogate(c)) {
                    if (!formattedCharSink.accept(i, style3, Character.toCodePoint(c2, c))) {
                        return false;
                    }
                    ++i;
                    continue;
                }
                if (formattedCharSink.accept(i, style3, 65533)) continue;
                return false;
            }
            if (StringDecomposer.feedChar(style3, formattedCharSink, i, c2)) continue;
            return false;
        }
        return true;
    }

    public static boolean iterateFormatted(FormattedText formattedText, Style style2, FormattedCharSink formattedCharSink) {
        return !formattedText.visit((style, string) -> StringDecomposer.iterateFormatted(string, 0, style, formattedCharSink) ? Optional.empty() : STOP_ITERATION, style2).isPresent();
    }

    public static String filterBrokenSurrogates(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        StringDecomposer.iterate(string, Style.EMPTY, (n, style, n2) -> {
            stringBuilder.appendCodePoint(n2);
            return true;
        });
        return stringBuilder.toString();
    }

    public static String getPlainText(FormattedText formattedText) {
        StringBuilder stringBuilder = new StringBuilder();
        StringDecomposer.iterateFormatted(formattedText, Style.EMPTY, (n, style, n2) -> {
            stringBuilder.appendCodePoint(n2);
            return true;
        });
        return stringBuilder.toString();
    }
}

