/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ProgressOption
extends Option {
    protected final float steps;
    protected final double minValue;
    protected double maxValue;
    private final Function<Options, Double> getter;
    private final BiConsumer<Options, Double> setter;
    private final BiFunction<Options, ProgressOption, Component> toString;

    public ProgressOption(String string, double d, double d2, float f, Function<Options, Double> function, BiConsumer<Options, Double> biConsumer, BiFunction<Options, ProgressOption, Component> biFunction) {
        super(string);
        this.minValue = d;
        this.maxValue = d2;
        this.steps = f;
        this.getter = function;
        this.setter = biConsumer;
        this.toString = biFunction;
    }

    @Override
    public AbstractWidget createButton(Options options, int n, int n2, int n3) {
        return new SliderButton(options, n, n2, n3, 20, this);
    }

    public double toPct(double d) {
        return Mth.clamp((this.clamp(d) - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
    }

    public double toValue(double d) {
        return this.clamp(Mth.lerp(Mth.clamp(d, 0.0, 1.0), this.minValue, this.maxValue));
    }

    private double clamp(double d) {
        if (this.steps > 0.0f) {
            d = this.steps * (float)Math.round(d / (double)this.steps);
        }
        return Mth.clamp(d, this.minValue, this.maxValue);
    }

    public double getMinValue() {
        return this.minValue;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(float f) {
        this.maxValue = f;
    }

    public void set(Options options, double d) {
        this.setter.accept(options, d);
    }

    public double get(Options options) {
        return this.getter.apply(options);
    }

    public Component getMessage(Options options) {
        return this.toString.apply(options, this);
    }
}

