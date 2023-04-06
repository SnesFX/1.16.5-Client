/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.nbt;

import net.minecraft.nbt.Tag;

public abstract class NumericTag
implements Tag {
    protected NumericTag() {
    }

    public abstract long getAsLong();

    public abstract int getAsInt();

    public abstract short getAsShort();

    public abstract byte getAsByte();

    public abstract double getAsDouble();

    public abstract float getAsFloat();

    public abstract Number getAsNumber();
}

