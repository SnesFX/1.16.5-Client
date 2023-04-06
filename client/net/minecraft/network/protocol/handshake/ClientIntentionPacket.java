/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.handshake;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;

public class ClientIntentionPacket
implements Packet<ServerHandshakePacketListener> {
    private int protocolVersion;
    private String hostName;
    private int port;
    private ConnectionProtocol intention;

    public ClientIntentionPacket() {
    }

    public ClientIntentionPacket(String string, int n, ConnectionProtocol connectionProtocol) {
        this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
        this.hostName = string;
        this.port = n;
        this.intention = connectionProtocol;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.protocolVersion = friendlyByteBuf.readVarInt();
        this.hostName = friendlyByteBuf.readUtf(255);
        this.port = friendlyByteBuf.readUnsignedShort();
        this.intention = ConnectionProtocol.getById(friendlyByteBuf.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.protocolVersion);
        friendlyByteBuf.writeUtf(this.hostName);
        friendlyByteBuf.writeShort(this.port);
        friendlyByteBuf.writeVarInt(this.intention.getId());
    }

    @Override
    public void handle(ServerHandshakePacketListener serverHandshakePacketListener) {
        serverHandshakePacketListener.handleIntention(this);
    }

    public ConnectionProtocol getIntention() {
        return this.intention;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}

