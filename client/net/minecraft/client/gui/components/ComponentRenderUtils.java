/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class ComponentRenderUtils {
    private static final FormattedCharSequence INDENT = FormattedCharSequence.codepoint(32, Style.EMPTY);

    private static String stripColor(String string) {
        return Minecraft.getInstance().options.chatColors ? string : ChatFormatting.stripFormatting(string);
    }

    public static List<FormattedCharSequence> wrapComponents(FormattedText formattedText2, int n, Font font) {
        ComponentCollector componentCollector = new ComponentCollector();
        formattedText2.visit((style, string) -> {
            componentCollector.append(FormattedText.of(ComponentRenderUtils.stripColor(string), style));
            return Optional.empty();
        }, Style.EMPTY);
        ArrayList arrayList = Lists.newArrayList();
        font.getSplitter().splitLines(componentCollector.getResultOrEmpty(), n, Style.EMPTY, (formattedText, bl) -> {
            FormattedCharSequence formattedCharSequence = Language.getInstance().getVisualOrder((FormattedText)formattedText);
            arrayList.add(bl != false ? FormattedCharSequence.composite(INDENT, formattedCharSequence) : formattedCharSequence);
        });
        if (arrayList.isEmpty()) {
            return Lists.newArrayList((Object[])new FormattedCharSequence[]{FormattedCharSequence.EMPTY});
        }
        return arrayList;
    }
}

