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
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundJigsawGeneratePacket
implements Packet<ServerGamePacketListener> {
    private BlockPos pos;
    private int levels;
    private boolean keepJigsaws;

    public ServerboundJigsawGeneratePacket() {
    }

    public ServerboundJigsawGeneratePacket(BlockPos blockPos, int n, boolean bl) {
        this.pos = blockPos;
        this.levels = n;
        this.keepJigsaws = bl;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.pos = friendlyByteBuf.readBlockPos();
        this.levels = friendlyByteBuf.readVarInt();
        this.keepJigsaws = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeVarInt(this.levels);
        friendlyByteBuf.writeBoolean(this.keepJigsaws);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleJigsawGenerate(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int levels() {
        return this.levels;
    }

    public boolean keepJigsaws() {
        return this.keepJigsaws;
    }
}

