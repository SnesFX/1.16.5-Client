/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgramManager {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void glUseProgram(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._glUseProgram(n);
    }

    public static void releaseProgram(Effect effect) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        effect.getFragmentProgram().close();
        effect.getVertexProgram().close();
        GlStateManager.glDeleteProgram(effect.getId());
    }

    public static int createProgram() throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        int n = GlStateManager.glCreateProgram();
        if (n <= 0) {
            throw new IOException("Could not create shader program (returned program ID " + n + ")");
        }
        return n;
    }

    public static void linkProgram(Effect effect) throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        effect.getFragmentProgram().attachToEffect(effect);
        effect.getVertexProgram().attachToEffect(effect);
        GlStateManager.glLinkProgram(effect.getId());
        int n = GlStateManager.glGetProgrami(effect.getId(), 35714);
        if (n == 0) {
            LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", (Object)effect.getVertexProgram().getName(), (Object)effect.getFragmentProgram().getName());
            LOGGER.warn(GlStateManager.glGetProgramInfoLog(effect.getId(), 32768));
        }
    }
}

