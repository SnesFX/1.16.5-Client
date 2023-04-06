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

public class ClientboundTeleportEntityPacket
implements Packet<ClientGamePacketListener> {
    private int id;
    private double x;
    private double y;
    private double z;
    private byte yRot;
    private byte xRot;
    private boolean onGround;

    public ClientboundTeleportEntityPacket() {
    }

    public ClientboundTeleportEntityPacket(Entity entity) {
        this.id = entity.getId();
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.yRot = (byte)(entity.yRot * 256.0f / 360.0f);
        this.xRot = (byte)(entity.xRot * 256.0f / 360.0f);
        this.onGround = entity.isOnGround();
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.id = friendlyByteBuf.readVarInt();
        this.x = friendlyByteBuf.readDouble();
        this.y = friendlyByteBuf.readDouble();
        this.z = friendlyByteBuf.readDouble();
        this.yRot = friendlyByteBuf.readByte();
        this.xRot = friendlyByteBuf.readByte();
        this.onGround = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.id);
        friendlyByteBuf.writeDouble(this.x);
        friendlyByteBuf.writeDouble(this.y);
        friendlyByteBuf.writeDouble(this.z);
        friendlyByteBuf.writeByte(this.yRot);
        friendlyByteBuf.writeByte(this.xRot);
        friendlyByteBuf.writeBoolean(this.onGround);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleTeleportEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }

    public boolean isOnGround() {
        return this.onGround;
    }
}

