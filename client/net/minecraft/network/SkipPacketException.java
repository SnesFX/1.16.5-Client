/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.network;

import io.netty.handler.codec.EncoderException;

public class SkipPacketException
extends EncoderException {
    public SkipPacketException(Throwable throwable) {
        super(throwable);
    }
}

