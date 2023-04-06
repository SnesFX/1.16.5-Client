/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class OptionButton
extends Button
implements TooltipAccessor {
    private final Option option;

    public OptionButton(int n, int n2, int n3, int n4, Option option, Component component, Button.OnPress onPress) {
        super(n, n2, n3, n4, component, onPress);
        this.option = option;
    }

    public Option getOption() {
        return this.option;
    }

    @Override
    public Optional<List<FormattedCharSequence>> getTooltip() {
        return this.option.getTooltip();
    }
}

