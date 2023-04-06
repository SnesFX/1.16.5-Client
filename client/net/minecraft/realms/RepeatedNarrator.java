/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 */
package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class RepeatedNarrator {
    private final float permitsPerSecond;
    private final AtomicReference<Params> params = new AtomicReference();

    public RepeatedNarrator(Duration duration) {
        this.permitsPerSecond = 1000.0f / (float)duration.toMillis();
    }

    public void narrate(String string) {
        Params params2 = this.params.updateAndGet(params -> {
            if (params == null || !string.equals(params.narration)) {
                return new Params(string, RateLimiter.create((double)this.permitsPerSecond));
            }
            return params;
        });
        if (params2.rateLimiter.tryAcquire(1)) {
            NarratorChatListener.INSTANCE.handle(ChatType.SYSTEM, new TextComponent(string), Util.NIL_UUID);
        }
    }

    static class Params {
        private final String narration;
        private final RateLimiter rateLimiter;

        Params(String string, RateLimiter rateLimiter) {
            this.narration = string;
            this.rateLimiter = rateLimiter;
        }
    }

}

