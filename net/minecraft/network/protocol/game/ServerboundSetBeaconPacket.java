/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundSetBeaconPacket
implements Packet<ServerGamePacketListener> {
    private int primary;
    private int secondary;

    public ServerboundSetBeaconPacket() {
    }

    public ServerboundSetBeaconPacket(int n, int n2) {
        this.primary = n;
        this.secondary = n2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.primary = friendlyByteBuf.readVarInt();
        this.secondary = friendlyByteBuf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.primary);
        friendlyByteBuf.writeVarInt(this.secondary);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleSetBeaconPacket(this);
    }

    public int getPrimary() {
        return this.primary;
    }

    public int getSecondary() {
        return this.secondary;
    }
}

