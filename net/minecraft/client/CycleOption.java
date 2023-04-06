/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.network.chat.Component;

public class CycleOption
extends Option {
    private final BiConsumer<Options, Integer> setter;
    private final BiFunction<Options, CycleOption, Component> toString;

    public CycleOption(String string, BiConsumer<Options, Integer> biConsumer, BiFunction<Options, CycleOption, Component> biFunction) {
        super(string);
        this.setter = biConsumer;
        this.toString = biFunction;
    }

    public void toggle(Options options, int n) {
        this.setter.accept(options, n);
        options.save();
    }

    @Override
    public AbstractWidget createButton(Options options, int n, int n2, int n3) {
        return new OptionButton(n, n2, n3, 20, this, this.getMessage(options), button -> {
            this.toggle(options, 1);
            button.setMessage(this.getMessage(options));
        });
    }

    public Component getMessage(Options options) {
        return this.toString.apply(options, this);
    }
}

