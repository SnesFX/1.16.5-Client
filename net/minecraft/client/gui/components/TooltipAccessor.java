/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import java.util.List;
import java.util.Optional;
import net.minecraft.util.FormattedCharSequence;

public interface TooltipAccessor {
    public Optional<List<FormattedCharSequence>> getTooltip();
}

