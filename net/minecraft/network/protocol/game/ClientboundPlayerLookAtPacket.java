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
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundPlayerLookAtPacket
implements Packet<ClientGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private int entity;
    private EntityAnchorArgument.Anchor fromAnchor;
    private EntityAnchorArgument.Anchor toAnchor;
    private boolean atEntity;

    public ClientboundPlayerLookAtPacket() {
    }

    public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor anchor, double d, double d2, double d3) {
        this.fromAnchor = anchor;
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor anchor, Entity entity, EntityAnchorArgument.Anchor anchor2) {
        this.fromAnchor = anchor;
        this.entity = entity.getId();
        this.toAnchor = anchor2;
        Vec3 vec3 = anchor2.apply(entity);
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.atEntity = true;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.fromAnchor = friendlyByteBuf.readEnum(EntityAnchorArgument.Anchor.class);
        this.x = friendlyByteBuf.readDouble();
        this.y = friendlyByteBuf.readDouble();
        this.z = friendlyByteBuf.readDouble();
        if (friendlyByteBuf.readBoolean()) {
            this.atEntity = true;
            this.entity = friendlyByteBuf.readVarInt();
            this.toAnchor = friendlyByteBuf.readEnum(EntityAnchorArgument.Anchor.class);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.fromAnchor);
        friendlyByteBuf.writeDouble(this.x);
        friendlyByteBuf.writeDouble(this.y);
        friendlyByteBuf.writeDouble(this.z);
        friendlyByteBuf.writeBoolean(this.atEntity);
        if (this.atEntity) {
            friendlyByteBuf.writeVarInt(this.entity);
            friendlyByteBuf.writeEnum(this.toAnchor);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleLookAt(this);
    }

    public EntityAnchorArgument.Anchor getFromAnchor() {
        return this.fromAnchor;
    }

    @Nullable
    public Vec3 getPosition(Level level) {
        if (this.atEntity) {
            Entity entity = level.getEntity(this.entity);
            if (entity == null) {
                return new Vec3(this.x, this.y, this.z);
            }
            return this.toAnchor.apply(entity);
        }
        return new Vec3(this.x, this.y, this.z);
    }
}

