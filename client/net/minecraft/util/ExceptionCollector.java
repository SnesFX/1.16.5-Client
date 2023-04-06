/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import javax.annotation.Nullable;

public class ExceptionCollector<T extends Throwable> {
    @Nullable
    private T result;

    public void add(T t) {
        if (this.result == null) {
            this.result = t;
        } else {
            ((Throwable)this.result).addSuppressed((Throwable)t);
        }
    }

    public void throwIfPresent() throws Throwable {
        if (this.result != null) {
            throw this.result;
        }
    }
}

