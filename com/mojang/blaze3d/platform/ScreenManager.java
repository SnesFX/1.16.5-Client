/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  javax.annotation.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWMonitorCallback
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.MonitorCreator;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

public class ScreenManager {
    private final Long2ObjectMap<Monitor> monitors = new Long2ObjectOpenHashMap();
    private final MonitorCreator monitorCreator;

    public ScreenManager(MonitorCreator monitorCreator) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        this.monitorCreator = monitorCreator;
        GLFW.glfwSetMonitorCallback((arg_0, arg_1) -> this.onMonitorChange(arg_0, arg_1));
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        if (pointerBuffer != null) {
            for (int i = 0; i < pointerBuffer.limit(); ++i) {
                long l = pointerBuffer.get(i);
                this.monitors.put(l, (Object)monitorCreator.createMonitor(l));
            }
        }
    }

    private void onMonitorChange(long l, int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n == 262145) {
            this.monitors.put(l, (Object)this.monitorCreator.createMonitor(l));
        } else if (n == 262146) {
            this.monitors.remove(l);
        }
    }

    @Nullable
    public Monitor getMonitor(long l) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return (Monitor)this.monitors.get(l);
    }

    @Nullable
    public Monitor findBestMonitor(Window window) {
        long l = GLFW.glfwGetWindowMonitor((long)window.getWindow());
        if (l != 0L) {
            return this.getMonitor(l);
        }
        int n = window.getX();
        int n2 = n + window.getScreenWidth();
        int n3 = window.getY();
        int n4 = n3 + window.getScreenHeight();
        int n5 = -1;
        Monitor monitor = null;
        for (Monitor monitor2 : this.monitors.values()) {
            int n6;
            int n7 = monitor2.getX();
            int n8 = n7 + monitor2.getCurrentMode().getWidth();
            int n9 = monitor2.getY();
            int n10 = n9 + monitor2.getCurrentMode().getHeight();
            int n11 = ScreenManager.clamp(n, n7, n8);
            int n12 = ScreenManager.clamp(n2, n7, n8);
            int n13 = ScreenManager.clamp(n3, n9, n10);
            int n14 = ScreenManager.clamp(n4, n9, n10);
            int n15 = Math.max(0, n12 - n11);
            int n16 = n15 * (n6 = Math.max(0, n14 - n13));
            if (n16 <= n5) continue;
            monitor = monitor2;
            n5 = n16;
        }
        return monitor;
    }

    public static int clamp(int n, int n2, int n3) {
        if (n < n2) {
            return n2;
        }
        if (n > n3) {
            return n3;
        }
        return n;
    }

    public void shutdown() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
        if (gLFWMonitorCallback != null) {
            gLFWMonitorCallback.free();
        }
    }
}

