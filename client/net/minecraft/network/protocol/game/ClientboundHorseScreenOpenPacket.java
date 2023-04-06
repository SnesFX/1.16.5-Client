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

public class ClientboundHorseScreenOpenPacket
implements Packet<ClientGamePacketListener> {
    private int containerId;
    private int size;
    private int entityId;

    public ClientboundHorseScreenOpenPacket() {
    }

    public ClientboundHorseScreenOpenPacket(int n, int n2, int n3) {
        this.containerId = n;
        this.size = n2;
        this.entityId = n3;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleHorseScreenOpen(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readUnsignedByte();
        this.size = friendlyByteBuf.readVarInt();
        this.entityId = friendlyByteBuf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.containerId);
        friendlyByteBuf.writeVarInt(this.size);
        friendlyByteBuf.writeInt(this.entityId);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSize() {
        return this.size;
    }

    public int getEntityId() {
        return this.entityId;
    }
}

