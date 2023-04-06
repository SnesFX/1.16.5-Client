/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.Version
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  oshi.SystemInfo
 *  oshi.hardware.HardwareAbstractionLayer
 *  oshi.hardware.Processor
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Processor;

public class GLX {
    private static final Logger LOGGER = LogManager.getLogger();
    private static String capsString = "";
    private static String cpuInfo;
    private static final Map<Integer, String> LOOKUP_MAP;

    public static String getOpenGLVersionString() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return "NO CONTEXT";
        }
        return GlStateManager._getString(7937) + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
    }

    public static int _getRefreshRate(Window window) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        long l = GLFW.glfwGetWindowMonitor((long)window.getWindow());
        if (l == 0L) {
            l = GLFW.glfwGetPrimaryMonitor();
        }
        GLFWVidMode gLFWVidMode = l == 0L ? null : GLFW.glfwGetVideoMode((long)l);
        return gLFWVidMode == null ? 0 : gLFWVidMode.refreshRate();
    }

    public static String _getLWJGLVersion() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return Version.getVersion();
    }

    public static LongSupplier _initGlfw() {
        LongSupplier longSupplier;
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        Window.checkGlfwError((n, string) -> {
            throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", n, string));
        });
        ArrayList arrayList = Lists.newArrayList();
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((n, l) -> arrayList.add(String.format("GLFW error during init: [0x%X]%s", n, l)));
        if (GLFW.glfwInit()) {
            longSupplier = () -> (long)(GLFW.glfwGetTime() * 1.0E9);
            for (String string2 : arrayList) {
                LOGGER.error("GLFW error collected during initialization: {}", (Object)string2);
            }
        } else {
            throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on((String)",").join((Iterable)arrayList));
        }
        RenderSystem.setErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback);
        return longSupplier;
    }

    public static void _setGlfwErrorCallback(GLFWErrorCallbackI gLFWErrorCallbackI) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallbackI);
        if (gLFWErrorCallback != null) {
            gLFWErrorCallback.free();
        }
    }

    public static boolean _shouldClose(Window window) {
        return GLFW.glfwWindowShouldClose((long)window.getWindow());
    }

    public static void _setupNvFogDistance() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager._fogi(34138, 34139);
        }
    }

    public static void _init(int n, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLCapabilities gLCapabilities = GL.getCapabilities();
        capsString = "Using framebuffer using " + GlStateManager._init_fbo(gLCapabilities);
        try {
            Processor[] arrprocessor = new SystemInfo().getHardware().getProcessors();
            cpuInfo = String.format("%dx %s", new Object[]{arrprocessor.length, arrprocessor[0]}).replaceAll("\\s+", " ");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        GlDebug.enableDebugCallback(n, bl);
    }

    public static String _getCapsString() {
        return capsString;
    }

    public static String _getCpuInfo() {
        return cpuInfo == null ? "<unknown>" : cpuInfo;
    }

    public static void _renderCrosshair(int n, boolean bl, boolean bl2, boolean bl3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._disableTexture();
        GlStateManager._depthMask(false);
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        GL11.glLineWidth((float)4.0f);
        bufferBuilder.begin(1, DefaultVertexFormat.POSITION_COLOR);
        if (bl) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(n, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
        }
        if (bl2) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(0.0, n, 0.0).color(0, 0, 0, 255).endVertex();
        }
        if (bl3) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(0.0, 0.0, n).color(0, 0, 0, 255).endVertex();
        }
        tesselator.end();
        GL11.glLineWidth((float)2.0f);
        bufferBuilder.begin(1, DefaultVertexFormat.POSITION_COLOR);
        if (bl) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
            bufferBuilder.vertex(n, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
        }
        if (bl2) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 255, 0, 255).endVertex();
            bufferBuilder.vertex(0.0, n, 0.0).color(0, 255, 0, 255).endVertex();
        }
        if (bl3) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(127, 127, 255, 255).endVertex();
            bufferBuilder.vertex(0.0, 0.0, n).color(127, 127, 255, 255).endVertex();
        }
        tesselator.end();
        GL11.glLineWidth((float)1.0f);
        GlStateManager._depthMask(true);
        GlStateManager._enableTexture();
    }

    public static String getErrorString(int n) {
        return LOOKUP_MAP.get(n);
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T make(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    static {
        LOOKUP_MAP = GLX.make(Maps.newHashMap(), hashMap -> {
            hashMap.put(0, "No error");
            hashMap.put(1280, "Enum parameter is invalid for this function");
            hashMap.put(1281, "Parameter is invalid for this function");
            hashMap.put(1282, "Current state is invalid for this function");
            hashMap.put(1283, "Stack overflow");
            hashMap.put(1284, "Stack underflow");
            hashMap.put(1285, "Out of memory");
            hashMap.put(1286, "Operation on incomplete framebuffer");
            hashMap.put(1286, "Operation on incomplete framebuffer");
        });
    }
}

