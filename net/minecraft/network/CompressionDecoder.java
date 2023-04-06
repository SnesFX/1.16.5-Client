/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.DecoderException
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;
import net.minecraft.network.FriendlyByteBuf;

public class CompressionDecoder
extends ByteToMessageDecoder {
    private final Inflater inflater;
    private int threshold;

    public CompressionDecoder(int n) {
        this.threshold = n;
        this.inflater = new Inflater();
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(byteBuf);
        int n = friendlyByteBuf.readVarInt();
        if (n == 0) {
            list.add((Object)friendlyByteBuf.readBytes(friendlyByteBuf.readableBytes()));
        } else {
            if (n < this.threshold) {
                throw new DecoderException("Badly compressed packet - size of " + n + " is below server threshold of " + this.threshold);
            }
            if (n > 2097152) {
                throw new DecoderException("Badly compressed packet - size of " + n + " is larger than protocol maximum of " + 2097152);
            }
            byte[] arrby = new byte[friendlyByteBuf.readableBytes()];
            friendlyByteBuf.readBytes(arrby);
            this.inflater.setInput(arrby);
            byte[] arrby2 = new byte[n];
            this.inflater.inflate(arrby2);
            list.add((Object)Unpooled.wrappedBuffer((byte[])arrby2));
            this.inflater.reset();
        }
    }

    public void setThreshold(int n) {
        this.threshold = n;
    }
}

