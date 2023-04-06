/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.FriendlyByteBuf;

@ChannelHandler.Sharable
public class Varint21LengthFieldPrepender
extends MessageToByteEncoder<ByteBuf> {
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        int n = byteBuf.readableBytes();
        int n2 = FriendlyByteBuf.getVarIntSize(n);
        if (n2 > 3) {
            throw new IllegalArgumentException("unable to fit " + n + " into " + 3);
        }
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(byteBuf2);
        friendlyByteBuf.ensureWritable(n2 + n);
        friendlyByteBuf.writeVarInt(n);
        friendlyByteBuf.writeBytes(byteBuf, byteBuf.readerIndex(), n);
    }

    protected /* synthetic */ void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        this.encode(channelHandlerContext, (ByteBuf)object, byteBuf);
    }
}

