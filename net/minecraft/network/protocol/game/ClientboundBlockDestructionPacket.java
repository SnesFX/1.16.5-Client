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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundBlockDestructionPacket
implements Packet<ClientGamePacketListener> {
    private int id;
    private BlockPos pos;
    private int progress;

    public ClientboundBlockDestructionPacket() {
    }

    public ClientboundBlockDestructionPacket(int n, BlockPos blockPos, int n2) {
        this.id = n;
        this.pos = blockPos;
        this.progress = n2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.id = friendlyByteBuf.readVarInt();
        this.pos = friendlyByteBuf.readBlockPos();
        this.progress = friendlyByteBuf.readUnsignedByte();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.id);
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeByte(this.progress);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleBlockDestruction(this);
    }

    public int getId() {
        return this.id;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getProgress() {
        return this.progress;
    }
}

