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

public class ServerboundMovePlayerPacket
implements Packet<ServerGamePacketListener> {
    protected double x;
    protected double y;
    protected double z;
    protected float yRot;
    protected float xRot;
    protected boolean onGround;
    protected boolean hasPos;
    protected boolean hasRot;

    public ServerboundMovePlayerPacket() {
    }

    public ServerboundMovePlayerPacket(boolean bl) {
        this.onGround = bl;
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleMovePlayer(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.onGround = friendlyByteBuf.readUnsignedByte() != 0;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.onGround ? 1 : 0);
    }

    public double getX(double d) {
        return this.hasPos ? this.x : d;
    }

    public double getY(double d) {
        return this.hasPos ? this.y : d;
    }

    public double getZ(double d) {
        return this.hasPos ? this.z : d;
    }

    public float getYRot(float f) {
        return this.hasRot ? this.yRot : f;
    }

    public float getXRot(float f) {
        return this.hasRot ? this.xRot : f;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public static class Rot
    extends ServerboundMovePlayerPacket {
        public Rot() {
            this.hasRot = true;
        }

        public Rot(float f, float f2, boolean bl) {
            this.yRot = f;
            this.xRot = f2;
            this.onGround = bl;
            this.hasRot = true;
        }

        @Override
        public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
            this.yRot = friendlyByteBuf.readFloat();
            this.xRot = friendlyByteBuf.readFloat();
            super.read(friendlyByteBuf);
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
            friendlyByteBuf.writeFloat(this.yRot);
            friendlyByteBuf.writeFloat(this.xRot);
            super.write(friendlyByteBuf);
        }
    }

    public static class Pos
    extends ServerboundMovePlayerPacket {
        public Pos() {
            this.hasPos = true;
        }

        public Pos(double d, double d2, double d3, boolean bl) {
            this.x = d;
            this.y = d2;
            this.z = d3;
            this.onGround = bl;
            this.hasPos = true;
        }

        @Override
        public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
            this.x = friendlyByteBuf.readDouble();
            this.y = friendlyByteBuf.readDouble();
            this.z = friendlyByteBuf.readDouble();
            super.read(friendlyByteBuf);
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
            friendlyByteBuf.writeDouble(this.x);
            friendlyByteBuf.writeDouble(this.y);
            friendlyByteBuf.writeDouble(this.z);
            super.write(friendlyByteBuf);
        }
    }

    public static class PosRot
    extends ServerboundMovePlayerPacket {
        public PosRot() {
            this.hasPos = true;
            this.hasRot = true;
        }

        public PosRot(double d, double d2, double d3, float f, float f2, boolean bl) {
            this.x = d;
            this.y = d2;
            this.z = d3;
            this.yRot = f;
            this.xRot = f2;
            this.onGround = bl;
            this.hasRot = true;
            this.hasPos = true;
        }

        @Override
        public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
            this.x = friendlyByteBuf.readDouble();
            this.y = friendlyByteBuf.readDouble();
            this.z = friendlyByteBuf.readDouble();
            this.yRot = friendlyByteBuf.readFloat();
            this.xRot = friendlyByteBuf.readFloat();
            super.read(friendlyByteBuf);
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
            friendlyByteBuf.writeDouble(this.x);
            friendlyByteBuf.writeDouble(this.y);
            friendlyByteBuf.writeDouble(this.z);
            friendlyByteBuf.writeFloat(this.yRot);
            friendlyByteBuf.writeFloat(this.xRot);
            super.write(friendlyByteBuf);
        }
    }

}

