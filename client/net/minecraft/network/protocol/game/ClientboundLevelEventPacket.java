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

public class ClientboundLevelEventPacket
implements Packet<ClientGamePacketListener> {
    private int type;
    private BlockPos pos;
    private int data;
    private boolean globalEvent;

    public ClientboundLevelEventPacket() {
    }

    public ClientboundLevelEventPacket(int n, BlockPos blockPos, int n2, boolean bl) {
        this.type = n;
        this.pos = blockPos.immutable();
        this.data = n2;
        this.globalEvent = bl;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.type = friendlyByteBuf.readInt();
        this.pos = friendlyByteBuf.readBlockPos();
        this.data = friendlyByteBuf.readInt();
        this.globalEvent = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeInt(this.type);
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeInt(this.data);
        friendlyByteBuf.writeBoolean(this.globalEvent);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleLevelEvent(this);
    }

    public boolean isGlobalEvent() {
        return this.globalEvent;
    }

    public int getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

