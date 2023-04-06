/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.schedule;

public class Keyframe {
    private final int timeStamp;
    private final float value;

    public Keyframe(int n, float f) {
        this.timeStamp = n;
        this.value = f;
    }

    public int getTimeStamp() {
        return this.timeStamp;
    }

    public float getValue() {
        return this.value;
    }
}

