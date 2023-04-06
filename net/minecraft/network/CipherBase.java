/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.ChannelHandlerContext
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class CipherBase {
    private final Cipher cipher;
    private byte[] heapIn = new byte[0];
    private byte[] heapOut = new byte[0];

    protected CipherBase(Cipher cipher) {
        this.cipher = cipher;
    }

    private byte[] bufToByte(ByteBuf byteBuf) {
        int n = byteBuf.readableBytes();
        if (this.heapIn.length < n) {
            this.heapIn = new byte[n];
        }
        byteBuf.readBytes(this.heapIn, 0, n);
        return this.heapIn;
    }

    protected ByteBuf decipher(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws ShortBufferException {
        int n = byteBuf.readableBytes();
        byte[] arrby = this.bufToByte(byteBuf);
        ByteBuf byteBuf2 = channelHandlerContext.alloc().heapBuffer(this.cipher.getOutputSize(n));
        byteBuf2.writerIndex(this.cipher.update(arrby, 0, n, byteBuf2.array(), byteBuf2.arrayOffset()));
        return byteBuf2;
    }

    protected void encipher(ByteBuf byteBuf, ByteBuf byteBuf2) throws ShortBufferException {
        int n = byteBuf.readableBytes();
        byte[] arrby = this.bufToByte(byteBuf);
        int n2 = this.cipher.getOutputSize(n);
        if (this.heapOut.length < n2) {
            this.heapOut = new byte[n2];
        }
        byteBuf2.writeBytes(this.heapOut, 0, this.cipher.update(arrby, 0, n, this.heapOut));
    }
}

