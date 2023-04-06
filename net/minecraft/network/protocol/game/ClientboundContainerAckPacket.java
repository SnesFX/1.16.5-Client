/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundContainerAckPacket
implements Packet<ClientGamePacketListener> {
    private int containerId;
    private short uid;
    private boolean accepted;

    public ClientboundContainerAckPacket() {
    }

    public ClientboundContainerAckPacket(int n, short s, boolean bl) {
        this.containerId = n;
        this.uid = s;
        this.accepted = bl;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleContainerAck(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readUnsignedByte();
        this.uid = friendlyByteBuf.readShort();
        this.accepted = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.containerId);
        friendlyByteBuf.writeShort(this.uid);
        friendlyByteBuf.writeBoolean(this.accepted);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public short getUid() {
        return this.uid;
    }

    public boolean isAccepted() {
        return this.accepted;
    }
}

