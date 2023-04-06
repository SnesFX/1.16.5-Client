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

public class ServerboundSignUpdatePacket
implements Packet<ServerGamePacketListener> {
    private BlockPos pos;
    private String[] lines;

    public ServerboundSignUpdatePacket() {
    }

    public ServerboundSignUpdatePacket(BlockPos blockPos, String string, String string2, String string3, String string4) {
        this.pos = blockPos;
        this.lines = new String[]{string, string2, string3, string4};
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.pos = friendlyByteBuf.readBlockPos();
        this.lines = new String[4];
        for (int i = 0; i < 4; ++i) {
            this.lines[i] = friendlyByteBuf.readUtf(384);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeBlockPos(this.pos);
        for (int i = 0; i < 4; ++i) {
            friendlyByteBuf.writeUtf(this.lines[i]);
        }
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleSignUpdate(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public String[] getLines() {
        return this.lines;
    }
}

