/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.platform;

import java.util.OptionalInt;

public class DisplayData {
    public final int width;
    public final int height;
    public final OptionalInt fullscreenWidth;
    public final OptionalInt fullscreenHeight;
    public final boolean isFullscreen;

    public DisplayData(int n, int n2, OptionalInt optionalInt, OptionalInt optionalInt2, boolean bl) {
        this.width = n;
        this.height = n2;
        this.fullscreenWidth = optionalInt;
        this.fullscreenHeight = optionalInt2;
        this.isFullscreen = bl;
    }
}

