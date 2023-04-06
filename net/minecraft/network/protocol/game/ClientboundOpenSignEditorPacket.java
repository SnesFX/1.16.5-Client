/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundOpenSignEditorPacket
implements Packet<ClientGamePacketListener> {
    private BlockPos pos;

    public ClientboundOpenSignEditorPacket() {
    }

    public ClientboundOpenSignEditorPacket(BlockPos blockPos) {
        this.pos = blockPos;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleOpenSignEditor(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.pos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeBlockPos(this.pos);
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

