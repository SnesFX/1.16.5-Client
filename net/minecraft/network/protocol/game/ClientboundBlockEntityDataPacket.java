/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundBlockEntityDataPacket
implements Packet<ClientGamePacketListener> {
    private BlockPos pos;
    private int type;
    private CompoundTag tag;

    public ClientboundBlockEntityDataPacket() {
    }

    public ClientboundBlockEntityDataPacket(BlockPos blockPos, int n, CompoundTag compoundTag) {
        this.pos = blockPos;
        this.type = n;
        this.tag = compoundTag;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.pos = friendlyByteBuf.readBlockPos();
        this.type = friendlyByteBuf.readUnsignedByte();
        this.tag = friendlyByteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeByte((byte)this.type);
        friendlyByteBuf.writeNbt(this.tag);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleBlockEntityData(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getType() {
        return this.type;
    }

    public CompoundTag getTag() {
        return this.tag;
    }
}

