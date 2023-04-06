/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

import java.util.function.Supplier;

public class LazyLoadedValue<T> {
    private Supplier<T> factory;
    private T value;

    public LazyLoadedValue(Supplier<T> supplier) {
        this.factory = supplier;
    }

    public T get() {
        Supplier<T> supplier = this.factory;
        if (supplier != null) {
            this.value = supplier.get();
            this.factory = null;
        }
        return this.value;
    }
}

