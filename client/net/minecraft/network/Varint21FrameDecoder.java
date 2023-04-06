/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.CorruptedFrameException
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public class Varint21FrameDecoder
extends ByteToMessageDecoder {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        byte[] arrby = new byte[3];
        for (int i = 0; i < arrby.length; ++i) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }
            arrby[i] = byteBuf.readByte();
            if (arrby[i] < 0) continue;
            FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])arrby));
            try {
                int n = friendlyByteBuf.readVarInt();
                if (byteBuf.readableBytes() < n) {
                    byteBuf.resetReaderIndex();
                    return;
                }
                list.add((Object)byteBuf.readBytes(n));
                return;
            }
            finally {
                friendlyByteBuf.release();
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}

