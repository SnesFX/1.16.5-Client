/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;

public class ServerboundCustomQueryPacket
implements Packet<ServerLoginPacketListener> {
    private int transactionId;
    private FriendlyByteBuf data;

    public ServerboundCustomQueryPacket() {
    }

    public ServerboundCustomQueryPacket(int n, @Nullable FriendlyByteBuf friendlyByteBuf) {
        this.transactionId = n;
        this.data = friendlyByteBuf;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.transactionId = friendlyByteBuf.readVarInt();
        if (friendlyByteBuf.readBoolean()) {
            int n = friendlyByteBuf.readableBytes();
            if (n < 0 || n > 1048576) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }
            this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(n));
        } else {
            this.data = null;
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.transactionId);
        if (this.data != null) {
            friendlyByteBuf.writeBoolean(true);
            friendlyByteBuf.writeBytes(this.data.copy());
        } else {
            friendlyByteBuf.writeBoolean(false);
        }
    }

    @Override
    public void handle(ServerLoginPacketListener serverLoginPacketListener) {
        serverLoginPacketListener.handleCustomQueryPacket(this);
    }
}

