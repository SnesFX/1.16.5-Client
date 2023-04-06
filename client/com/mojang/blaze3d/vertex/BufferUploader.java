/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

public class BufferUploader {
    public static void end(BufferBuilder bufferBuilder) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                Pair<BufferBuilder.DrawState, ByteBuffer> pair = bufferBuilder.popNextBuffer();
                BufferBuilder.DrawState drawState = (BufferBuilder.DrawState)pair.getFirst();
                BufferUploader._end((ByteBuffer)pair.getSecond(), drawState.mode(), drawState.format(), drawState.vertexCount());
            });
        } else {
            Pair<BufferBuilder.DrawState, ByteBuffer> pair = bufferBuilder.popNextBuffer();
            BufferBuilder.DrawState drawState = (BufferBuilder.DrawState)pair.getFirst();
            BufferUploader._end((ByteBuffer)pair.getSecond(), drawState.mode(), drawState.format(), drawState.vertexCount());
        }
    }

    private static void _end(ByteBuffer byteBuffer, int n, VertexFormat vertexFormat, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        byteBuffer.clear();
        if (n2 <= 0) {
            return;
        }
        vertexFormat.setupBufferState(MemoryUtil.memAddress((ByteBuffer)byteBuffer));
        GlStateManager._drawArrays(n, 0, n2);
        vertexFormat.clearBufferState();
    }
}

