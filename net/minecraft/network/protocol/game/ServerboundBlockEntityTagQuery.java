/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundBlockEntityTagQuery
implements Packet<ServerGamePacketListener> {
    private int transactionId;
    private BlockPos pos;

    public ServerboundBlockEntityTagQuery() {
    }

    public ServerboundBlockEntityTagQuery(int n, BlockPos blockPos) {
        this.transactionId = n;
        this.pos = blockPos;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.transactionId = friendlyByteBuf.readVarInt();
        this.pos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.transactionId);
        friendlyByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleBlockEntityTagQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

