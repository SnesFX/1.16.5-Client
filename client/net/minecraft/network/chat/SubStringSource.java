/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 */
package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;

public class SubStringSource {
    private final String plainText;
    private final List<Style> charStyles;
    private final Int2IntFunction reverseCharModifier;

    private SubStringSource(String string, List<Style> list, Int2IntFunction int2IntFunction) {
        this.plainText = string;
        this.charStyles = ImmutableList.copyOf(list);
        this.reverseCharModifier = int2IntFunction;
    }

    public String getPlainText() {
        return this.plainText;
    }

    public List<FormattedCharSequence> substring(int n, int n2, boolean bl) {
        if (n2 == 0) {
            return ImmutableList.of();
        }
        ArrayList arrayList = Lists.newArrayList();
        Style style = this.charStyles.get(n);
        int n3 = n;
        for (int i = 1; i < n2; ++i) {
            int n4 = n + i;
            Style style2 = this.charStyles.get(n4);
            if (style2.equals(style)) continue;
            String string = this.plainText.substring(n3, n4);
            arrayList.add(bl ? FormattedCharSequence.backward(string, style, this.reverseCharModifier) : FormattedCharSequence.forward(string, style));
            style = style2;
            n3 = n4;
        }
        if (n3 < n + n2) {
            String string = this.plainText.substring(n3, n + n2);
            arrayList.add(bl ? FormattedCharSequence.backward(string, style, this.reverseCharModifier) : FormattedCharSequence.forward(string, style));
        }
        return bl ? Lists.reverse((List)arrayList) : arrayList;
    }

    public static SubStringSource create(FormattedText formattedText, Int2IntFunction int2IntFunction, UnaryOperator<String> unaryOperator) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList arrayList = Lists.newArrayList();
        formattedText.visit((style2, string) -> {
            StringDecomposer.iterateFormatted(string, style2, (n, style, n2) -> {
                stringBuilder.appendCodePoint(n2);
                int n3 = Character.charCount(n2);
                for (int i = 0; i < n3; ++i) {
                    arrayList.add(style);
                }
                return true;
            });
            return Optional.empty();
        }, Style.EMPTY);
        return new SubStringSource((String)unaryOperator.apply(stringBuilder.toString()), arrayList, int2IntFunction);
    }
}

