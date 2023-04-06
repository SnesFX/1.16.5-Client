/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.ibm.icu.lang.UCharacter
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.Bidi
 *  com.ibm.icu.text.BidiRun
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.SubStringSource;
import net.minecraft.util.FormattedCharSequence;

public class FormattedBidiReorder {
    public static FormattedCharSequence reorder(FormattedText formattedText, boolean bl) {
        SubStringSource subStringSource = SubStringSource.create(formattedText, UCharacter::getMirror, FormattedBidiReorder::shape);
        Bidi bidi = new Bidi(subStringSource.getPlainText(), bl ? 127 : 126);
        bidi.setReorderingMode(0);
        ArrayList arrayList = Lists.newArrayList();
        int n = bidi.countRuns();
        for (int i = 0; i < n; ++i) {
            BidiRun bidiRun = bidi.getVisualRun(i);
            arrayList.addAll(subStringSource.substring(bidiRun.getStart(), bidiRun.getLength(), bidiRun.isOddRun()));
        }
        return FormattedCharSequence.composite(arrayList);
    }

    private static String shape(String string) {
        try {
            return new ArabicShaping(8).shape(string);
        }
        catch (Exception exception) {
            return string;
        }
    }
}

