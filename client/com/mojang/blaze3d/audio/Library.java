/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.openal.AL
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.ALC
 *  org.lwjgl.openal.ALC10
 *  org.lwjgl.openal.ALCCapabilities
 *  org.lwjgl.openal.ALCapabilities
 *  org.lwjgl.system.MemoryStack
 */
package com.mojang.blaze3d.audio;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.OpenAlUtil;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

public class Library {
    private static final Logger LOGGER = LogManager.getLogger();
    private long device;
    private long context;
    private static final ChannelPool EMPTY = new ChannelPool(){

        @Nullable
        @Override
        public Channel acquire() {
            return null;
        }

        @Override
        public boolean release(Channel channel) {
            return false;
        }

        @Override
        public void cleanup() {
        }

        @Override
        public int getMaxCount() {
            return 0;
        }

        @Override
        public int getUsedCount() {
            return 0;
        }
    };
    private ChannelPool staticChannels = EMPTY;
    private ChannelPool streamingChannels = EMPTY;
    private final Listener listener = new Listener();

    public void init() {
        this.device = Library.tryOpenDevice();
        ALCCapabilities aLCCapabilities = ALC.createCapabilities((long)this.device);
        if (OpenAlUtil.checkALCError(this.device, "Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        }
        if (!aLCCapabilities.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        }
        this.context = ALC10.alcCreateContext((long)this.device, (IntBuffer)null);
        ALC10.alcMakeContextCurrent((long)this.context);
        int n = this.getChannelCount();
        int n2 = Mth.clamp((int)Mth.sqrt(n), 2, 8);
        int n3 = Mth.clamp(n - n2, 8, 255);
        this.staticChannels = new CountingChannelPool(n3);
        this.streamingChannels = new CountingChannelPool(n2);
        ALCapabilities aLCapabilities = AL.createCapabilities((ALCCapabilities)aLCCapabilities);
        OpenAlUtil.checkALError("Initialization");
        if (!aLCapabilities.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
        }
        AL10.alEnable((int)512);
        if (!aLCapabilities.AL_EXT_LINEAR_DISTANCE) {
            throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
        }
        OpenAlUtil.checkALError("Enable per-source distance models");
        LOGGER.info("OpenAL initialized.");
    }

    private int getChannelCount() {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int n = ALC10.alcGetInteger((long)this.device, (int)4098);
            if (OpenAlUtil.checkALCError(this.device, "Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            IntBuffer intBuffer = memoryStack.mallocInt(n);
            ALC10.alcGetIntegerv((long)this.device, (int)4099, (IntBuffer)intBuffer);
            if (OpenAlUtil.checkALCError(this.device, "Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            int n2 = 0;
            while (n2 < n) {
                int n3;
                if ((n3 = intBuffer.get(n2++)) == 0) {
                    break;
                }
                int n4 = intBuffer.get(n2++);
                if (n3 != 4112) continue;
                int n5 = n4;
                return n5;
            }
        }
        return 30;
    }

    private static long tryOpenDevice() {
        for (int i = 0; i < 3; ++i) {
            long l = ALC10.alcOpenDevice((ByteBuffer)null);
            if (l == 0L || OpenAlUtil.checkALCError(l, "Open device")) continue;
            return l;
        }
        throw new IllegalStateException("Failed to open OpenAL device");
    }

    public void cleanup() {
        this.staticChannels.cleanup();
        this.streamingChannels.cleanup();
        ALC10.alcDestroyContext((long)this.context);
        if (this.device != 0L) {
            ALC10.alcCloseDevice((long)this.device);
        }
    }

    public Listener getListener() {
        return this.listener;
    }

    @Nullable
    public Channel acquireChannel(Pool pool) {
        return (pool == Pool.STREAMING ? this.streamingChannels : this.staticChannels).acquire();
    }

    public void releaseChannel(Channel channel) {
        if (!this.staticChannels.release(channel) && !this.streamingChannels.release(channel)) {
            throw new IllegalStateException("Tried to release unknown channel");
        }
    }

    public String getDebugString() {
        return String.format("Sounds: %d/%d + %d/%d", this.staticChannels.getUsedCount(), this.staticChannels.getMaxCount(), this.streamingChannels.getUsedCount(), this.streamingChannels.getMaxCount());
    }

    static class CountingChannelPool
    implements ChannelPool {
        private final int limit;
        private final Set<Channel> activeChannels = Sets.newIdentityHashSet();

        public CountingChannelPool(int n) {
            this.limit = n;
        }

        @Nullable
        @Override
        public Channel acquire() {
            if (this.activeChannels.size() >= this.limit) {
                LOGGER.warn("Maximum sound pool size {} reached", (Object)this.limit);
                return null;
            }
            Channel channel = Channel.create();
            if (channel != null) {
                this.activeChannels.add(channel);
            }
            return channel;
        }

        @Override
        public boolean release(Channel channel) {
            if (!this.activeChannels.remove(channel)) {
                return false;
            }
            channel.destroy();
            return true;
        }

        @Override
        public void cleanup() {
            this.activeChannels.forEach(Channel::destroy);
            this.activeChannels.clear();
        }

        @Override
        public int getMaxCount() {
            return this.limit;
        }

        @Override
        public int getUsedCount() {
            return this.activeChannels.size();
        }
    }

    static interface ChannelPool {
        @Nullable
        public Channel acquire();

        public boolean release(Channel var1);

        public void cleanup();

        public int getMaxCount();

        public int getUsedCount();
    }

    public static enum Pool {
        STATIC,
        STREAMING;
        
    }

}

