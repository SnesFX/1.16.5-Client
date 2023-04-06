/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundMoveEntityPacket
implements Packet<ClientGamePacketListener> {
    protected int entityId;
    protected short xa;
    protected short ya;
    protected short za;
    protected byte yRot;
    protected byte xRot;
    protected boolean onGround;
    protected boolean hasRot;
    protected boolean hasPos;

    public static long entityToPacket(double d) {
        return Mth.lfloor(d * 4096.0);
    }

    public static double packetToEntity(long l) {
        return (double)l / 4096.0;
    }

    public Vec3 updateEntityPosition(Vec3 vec3) {
        double d = this.xa == 0 ? vec3.x : ClientboundMoveEntityPacket.packetToEntity(ClientboundMoveEntityPacket.entityToPacket(vec3.x) + (long)this.xa);
        double d2 = this.ya == 0 ? vec3.y : ClientboundMoveEntityPacket.packetToEntity(ClientboundMoveEntityPacket.entityToPacket(vec3.y) + (long)this.ya);
        double d3 = this.za == 0 ? vec3.z : ClientboundMoveEntityPacket.packetToEntity(ClientboundMoveEntityPacket.entityToPacket(vec3.z) + (long)this.za);
        return new Vec3(d, d2, d3);
    }

    public static Vec3 packetToEntity(long l, long l2, long l3) {
        return new Vec3(l, l2, l3).scale(2.44140625E-4);
    }

    public ClientboundMoveEntityPacket() {
    }

    public ClientboundMoveEntityPacket(int n) {
        this.entityId = n;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.entityId = friendlyByteBuf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.entityId);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleMoveEntity(this);
    }

    public String toString() {
        return "Entity_" + super.toString();
    }

    @Nullable
    public Entity getEntity(Level level) {
        return level.getEntity(this.entityId);
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }

    public boolean hasRotation() {
        return this.hasRot;
    }

    public boolean hasPosition() {
        return this.hasPos;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public static class Rot
    extends ClientboundMoveEntityPacket {
        public Rot() {
            this.hasRot = true;
        }

        public Rot(int n, byte by, byte by2, boolean bl) {
            super(n);
            this.yRot = by;
            this.xRot = by2;
            this.hasRot = true;
            this.onGround = bl;
        }

        @Override
        public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
            super.read(friendlyByteBuf);
            this.yRot = friendlyByteBuf.readByte();
            this.xRot = friendlyByteBuf.readByte();
            this.onGround = friendlyByteBuf.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeByte(this.yRot);
            friendlyByteBuf.writeByte(this.xRot);
            friendlyByteBuf.writeBoolean(this.onGround);
        }
    }

    public static class Pos
    extends ClientboundMoveEntityPacket {
        public Pos() {
            this.hasPos = true;
        }

        public Pos(int n, short s, short s2, short s3, boolean bl) {
            super(n);
            this.xa = s;
            this.ya = s2;
            this.za = s3;
            this.onGround = bl;
            this.hasPos = true;
        }

        @Override
        public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
            super.read(friendlyByteBuf);
            this.xa = friendlyByteBuf.readShort();
            this.ya = friendlyByteBuf.readShort();
            this.za = friendlyByteBuf.readShort();
            this.onGround = friendlyByteBuf.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeShort(this.xa);
            friendlyByteBuf.writeShort(this.ya);
            friendlyByteBuf.writeShort(this.za);
            friendlyByteBuf.writeBoolean(this.onGround);
        }
    }

    public static class PosRot
    extends ClientboundMoveEntityPacket {
        public PosRot() {
            this.hasRot = true;
            this.hasPos = true;
        }

        public PosRot(int n, short s, short s2, short s3, byte by, byte by2, boolean bl) {
            super(n);
            this.xa = s;
            this.ya = s2;
            this.za = s3;
            this.yRot = by;
            this.xRot = by2;
            this.onGround = bl;
            this.hasRot = true;
            this.hasPos = true;
        }

        @Override
        public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
            super.read(friendlyByteBuf);
            this.xa = friendlyByteBuf.readShort();
            this.ya = friendlyByteBuf.readShort();
            this.za = friendlyByteBuf.readShort();
            this.yRot = friendlyByteBuf.readByte();
            this.xRot = friendlyByteBuf.readByte();
            this.onGround = friendlyByteBuf.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeShort(this.xa);
            friendlyByteBuf.writeShort(this.ya);
            friendlyByteBuf.writeShort(this.za);
            friendlyByteBuf.writeByte(this.yRot);
            friendlyByteBuf.writeByte(this.xRot);
            friendlyByteBuf.writeBoolean(this.onGround);
        }
    }

}

