/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToMessageDecoder
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;
import net.minecraft.network.CipherBase;

public class CipherDecoder
extends MessageToMessageDecoder<ByteBuf> {
    private final CipherBase cipher;

    public CipherDecoder(Cipher cipher) {
        this.cipher = new CipherBase(cipher);
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add((Object)this.cipher.decipher(channelHandlerContext, byteBuf));
    }

    protected /* synthetic */ void decode(ChannelHandlerContext channelHandlerContext, Object object, List list) throws Exception {
        this.decode(channelHandlerContext, (ByteBuf)object, (List<Object>)list);
    }
}

