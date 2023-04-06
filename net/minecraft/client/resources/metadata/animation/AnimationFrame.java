/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.metadata.animation;

public class AnimationFrame {
    private final int index;
    private final int time;

    public AnimationFrame(int n) {
        this(n, -1);
    }

    public AnimationFrame(int n, int n2) {
        this.index = n;
        this.time = n2;
    }

    public boolean isTimeUnknown() {
        return this.time == -1;
    }

    public int getTime() {
        return this.time;
    }

    public int getIndex() {
        return this.index;
    }
}

