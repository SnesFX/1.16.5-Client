/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class AbstractOptionSliderButton
extends AbstractSliderButton {
    protected final Options options;

    protected AbstractOptionSliderButton(Options options, int n, int n2, int n3, int n4, double d) {
        super(n, n2, n3, n4, TextComponent.EMPTY, d);
        this.options = options;
    }
}

