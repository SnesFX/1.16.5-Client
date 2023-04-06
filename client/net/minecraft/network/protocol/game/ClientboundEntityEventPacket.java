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
import net.minecraft.world.level.Level;

public class ClientboundEntityEventPacket
implements Packet<ClientGamePacketListener> {
    private int entityId;
    private byte eventId;

    public ClientboundEntityEventPacket() {
    }

    public ClientboundEntityEventPacket(Entity entity, byte by) {
        this.entityId = entity.getId();
        this.eventId = by;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.entityId = friendlyByteBuf.readInt();
        this.eventId = friendlyByteBuf.readByte();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeInt(this.entityId);
        friendlyByteBuf.writeByte(this.eventId);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleEntityEvent(this);
    }

    public Entity getEntity(Level level) {
        return level.getEntity(this.entityId);
    }

    public byte getEventId() {
        return this.eventId;
    }
}

