/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

public class GlUtil {
    public static String getVendor() {
        return GlStateManager._getString(7936);
    }

    public static String getCpuInfo() {
        return GLX._getCpuInfo();
    }

    public static String getRenderer() {
        return GlStateManager._getString(7937);
    }

    public static String getOpenGLVersion() {
        return GlStateManager._getString(7938);
    }
}

