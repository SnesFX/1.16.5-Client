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
import net.minecraft.world.entity.Entity;

public class ServerboundMoveVehiclePacket
implements Packet<ServerGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private float yRot;
    private float xRot;

    public ServerboundMoveVehiclePacket() {
    }

    public ServerboundMoveVehiclePacket(Entity entity) {
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.yRot = entity.yRot;
        this.xRot = entity.xRot;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.x = friendlyByteBuf.readDouble();
        this.y = friendlyByteBuf.readDouble();
        this.z = friendlyByteBuf.readDouble();
        this.yRot = friendlyByteBuf.readFloat();
        this.xRot = friendlyByteBuf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeDouble(this.x);
        friendlyByteBuf.writeDouble(this.y);
        friendlyByteBuf.writeDouble(this.z);
        friendlyByteBuf.writeFloat(this.yRot);
        friendlyByteBuf.writeFloat(this.xRot);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleMoveVehicle(this);
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

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }
}

