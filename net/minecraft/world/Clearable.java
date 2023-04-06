/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import javax.annotation.Nullable;

public interface Clearable {
    public void clearContent();

    public static void tryClear(@Nullable Object object) {
        if (object instanceof Clearable) {
            ((Clearable)object).clearContent();
        }
    }
}

