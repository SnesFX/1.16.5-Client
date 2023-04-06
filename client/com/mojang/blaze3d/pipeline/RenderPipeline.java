/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderCall;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RenderPipeline {
    private final List<ConcurrentLinkedQueue<RenderCall>> renderCalls = ImmutableList.of(new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue());
    private volatile int recordingBuffer = this.processedBuffer = this.renderingBuffer + 1;
    private volatile int processedBuffer;
    private volatile int renderingBuffer;
}

