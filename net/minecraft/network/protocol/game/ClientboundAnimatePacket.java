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
import net.minecraft.world.entity.Entity;

public class ClientboundAnimatePacket
implements Packet<ClientGamePacketListener> {
    private int id;
    private int action;

    public ClientboundAnimatePacket() {
    }

    public ClientboundAnimatePacket(Entity entity, int n) {
        this.id = entity.getId();
        this.action = n;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.id = friendlyByteBuf.readVarInt();
        this.action = friendlyByteBuf.readUnsignedByte();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.id);
        friendlyByteBuf.writeByte(this.action);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleAnimate(this);
    }

    public int getId() {
        return this.id;
    }

    public int getAction() {
        return this.action;
    }
}

