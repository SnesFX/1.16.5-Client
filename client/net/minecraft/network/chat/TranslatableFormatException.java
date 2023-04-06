/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.chat;

import net.minecraft.network.chat.TranslatableComponent;

public class TranslatableFormatException
extends IllegalArgumentException {
    public TranslatableFormatException(TranslatableComponent translatableComponent, String string) {
        super(String.format("Error parsing: %s: %s", translatableComponent, string));
    }

    public TranslatableFormatException(TranslatableComponent translatableComponent, int n) {
        super(String.format("Invalid index %d requested for %s", n, translatableComponent));
    }

    public TranslatableFormatException(TranslatableComponent translatableComponent, Throwable throwable) {
        super(String.format("Error while parsing: %s", translatableComponent), throwable);
    }
}

