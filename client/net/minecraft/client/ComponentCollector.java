/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FormattedText;

public class ComponentCollector {
    private final List<FormattedText> parts = Lists.newArrayList();

    public void append(FormattedText formattedText) {
        this.parts.add(formattedText);
    }

    @Nullable
    public FormattedText getResult() {
        if (this.parts.isEmpty()) {
            return null;
        }
        if (this.parts.size() == 1) {
            return this.parts.get(0);
        }
        return FormattedText.composite(this.parts);
    }

    public FormattedText getResultOrEmpty() {
        FormattedText formattedText = this.getResult();
        return formattedText != null ? formattedText : FormattedText.EMPTY;
    }
}

