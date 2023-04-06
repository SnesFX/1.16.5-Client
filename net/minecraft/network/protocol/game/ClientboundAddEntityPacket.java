/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket
implements Packet<ClientGamePacketListener> {
    private int id;
    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private int xa;
    private int ya;
    private int za;
    private int xRot;
    private int yRot;
    private EntityType<?> type;
    private int data;

    public ClientboundAddEntityPacket() {
    }

    public ClientboundAddEntityPacket(int n, UUID uUID, double d, double d2, double d3, float f, float f2, EntityType<?> entityType, int n2, Vec3 vec3) {
        this.id = n;
        this.uuid = uUID;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.xRot = Mth.floor(f * 256.0f / 360.0f);
        this.yRot = Mth.floor(f2 * 256.0f / 360.0f);
        this.type = entityType;
        this.data = n2;
        this.xa = (int)(Mth.clamp(vec3.x, -3.9, 3.9) * 8000.0);
        this.ya = (int)(Mth.clamp(vec3.y, -3.9, 3.9) * 8000.0);
        this.za = (int)(Mth.clamp(vec3.z, -3.9, 3.9) * 8000.0);
    }

    public ClientboundAddEntityPacket(Entity entity) {
        this(entity, 0);
    }

    public ClientboundAddEntityPacket(Entity entity, int n) {
        this(entity.getId(), entity.getUUID(), entity.getX(), entity.getY(), entity.getZ(), entity.xRot, entity.yRot, entity.getType(), n, entity.getDeltaMovement());
    }

    public ClientboundAddEntityPacket(Entity entity, EntityType<?> entityType, int n, BlockPos blockPos) {
        this(entity.getId(), entity.getUUID(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), entity.xRot, entity.yRot, entityType, n, entity.getDeltaMovement());
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.id = friendlyByteBuf.readVarInt();
        this.uuid = friendlyByteBuf.readUUID();
        this.type = Registry.ENTITY_TYPE.byId(friendlyByteBuf.readVarInt());
        this.x = friendlyByteBuf.readDouble();
        this.y = friendlyByteBuf.readDouble();
        this.z = friendlyByteBuf.readDouble();
        this.xRot = friendlyByteBuf.readByte();
        this.yRot = friendlyByteBuf.readByte();
        this.data = friendlyByteBuf.readInt();
        this.xa = friendlyByteBuf.readShort();
        this.ya = friendlyByteBuf.readShort();
        this.za = friendlyByteBuf.readShort();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.id);
        friendlyByteBuf.writeUUID(this.uuid);
        friendlyByteBuf.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
        friendlyByteBuf.writeDouble(this.x);
        friendlyByteBuf.writeDouble(this.y);
        friendlyByteBuf.writeDouble(this.z);
        friendlyByteBuf.writeByte(this.xRot);
        friendlyByteBuf.writeByte(this.yRot);
        friendlyByteBuf.writeInt(this.data);
        friendlyByteBuf.writeShort(this.xa);
        friendlyByteBuf.writeShort(this.ya);
        friendlyByteBuf.writeShort(this.za);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleAddEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public UUID getUUID() {
        return this.uuid;
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

    public double getXa() {
        return (double)this.xa / 8000.0;
    }

    public double getYa() {
        return (double)this.ya / 8000.0;
    }

    public double getZa() {
        return (double)this.za / 8000.0;
    }

    public int getxRot() {
        return this.xRot;
    }

    public int getyRot() {
        return this.yRot;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }
}

