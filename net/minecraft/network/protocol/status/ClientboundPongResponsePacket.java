/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.status;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;

public class ClientboundPongResponsePacket
implements Packet<ClientStatusPacketListener> {
    private long time;

    public ClientboundPongResponsePacket() {
    }

    public ClientboundPongResponsePacket(long l) {
        this.time = l;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.time = friendlyByteBuf.readLong();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeLong(this.time);
    }

    @Override
    public void handle(ClientStatusPacketListener clientStatusPacketListener) {
        clientStatusPacketListener.handlePongResponse(this);
    }
}

