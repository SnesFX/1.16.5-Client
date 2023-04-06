/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;

public class ServerboundStatusRequestPacket
implements Packet<ServerStatusPacketListener> {
    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
    }

    @Override
    public void handle(ServerStatusPacketListener serverStatusPacketListener) {
        serverStatusPacketListener.handleStatusRequest(this);
    }
}

