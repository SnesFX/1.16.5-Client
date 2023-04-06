/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.font;

public interface GlyphInfo {
    public float getAdvance();

    default public float getAdvance(boolean bl) {
        return this.getAdvance() + (bl ? this.getBoldOffset() : 0.0f);
    }

    default public float getBearingX() {
        return 0.0f;
    }

    default public float getBoldOffset() {
        return 1.0f;
    }

    default public float getShadowOffset() {
        return 1.0f;
    }
}

