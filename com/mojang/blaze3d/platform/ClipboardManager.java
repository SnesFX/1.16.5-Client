/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import net.minecraft.util.StringDecomposer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

public class ClipboardManager {
    private final ByteBuffer clipboardScratchBuffer = BufferUtils.createByteBuffer((int)8192);

    public String getClipboard(long l, GLFWErrorCallbackI gLFWErrorCallbackI) {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallbackI);
        String string = GLFW.glfwGetClipboardString((long)l);
        string = string != null ? StringDecomposer.filterBrokenSurrogates(string) : "";
        GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback);
        if (gLFWErrorCallback2 != null) {
            gLFWErrorCallback2.free();
        }
        return string;
    }

    private static void pushClipboard(long l, ByteBuffer byteBuffer, byte[] arrby) {
        byteBuffer.clear();
        byteBuffer.put(arrby);
        byteBuffer.put((byte)0);
        byteBuffer.flip();
        GLFW.glfwSetClipboardString((long)l, (ByteBuffer)byteBuffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setClipboard(long l, String string) {
        byte[] arrby = string.getBytes(Charsets.UTF_8);
        int n = arrby.length + 1;
        if (n < this.clipboardScratchBuffer.capacity()) {
            ClipboardManager.pushClipboard(l, this.clipboardScratchBuffer, arrby);
        } else {
            ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)n);
            try {
                ClipboardManager.pushClipboard(l, byteBuffer, arrby);
            }
            finally {
                MemoryUtil.memFree((Buffer)byteBuffer);
            }
        }
    }
}

