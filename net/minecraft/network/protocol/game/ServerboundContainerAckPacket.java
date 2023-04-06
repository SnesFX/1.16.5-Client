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
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundContainerAckPacket
implements Packet<ServerGamePacketListener> {
    private int containerId;
    private short uid;
    private boolean accepted;

    public ServerboundContainerAckPacket() {
    }

    public ServerboundContainerAckPacket(int n, short s, boolean bl) {
        this.containerId = n;
        this.uid = s;
        this.accepted = bl;
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleContainerAck(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readByte();
        this.uid = friendlyByteBuf.readShort();
        this.accepted = friendlyByteBuf.readByte() != 0;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.containerId);
        friendlyByteBuf.writeShort(this.uid);
        friendlyByteBuf.writeByte(this.accepted ? 1 : 0);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public short getUid() {
        return this.uid;
    }
}

