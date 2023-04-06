/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.Component;

public class LogaritmicProgressOption
extends ProgressOption {
    public LogaritmicProgressOption(String string, double d, double d2, float f, Function<Options, Double> function, BiConsumer<Options, Double> biConsumer, BiFunction<Options, ProgressOption, Component> biFunction) {
        super(string, d, d2, f, function, biConsumer, biFunction);
    }

    @Override
    public double toPct(double d) {
        return Math.log(d / this.minValue) / Math.log(this.maxValue / this.minValue);
    }

    @Override
    public double toValue(double d) {
        return this.minValue * Math.pow(2.718281828459045, Math.log(this.maxValue / this.minValue) * d);
    }
}

