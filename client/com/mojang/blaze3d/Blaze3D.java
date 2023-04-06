/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class Blaze3D {
    public static void youJustLostTheGame() {
        MemoryUtil.memSet((long)0L, (int)0, (long)1L);
    }

    public static double getTime() {
        return GLFW.glfwGetTime();
    }
}

