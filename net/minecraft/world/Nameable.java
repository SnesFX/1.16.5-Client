/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public interface Nameable {
    public Component getName();

    default public boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    default public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    default public Component getCustomName() {
        return null;
    }
}

