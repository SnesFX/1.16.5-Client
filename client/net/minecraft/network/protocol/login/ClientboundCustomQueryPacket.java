/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomQueryPacket
implements Packet<ClientLoginPacketListener> {
    private int transactionId;
    private ResourceLocation identifier;
    private FriendlyByteBuf data;

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.transactionId = friendlyByteBuf.readVarInt();
        this.identifier = friendlyByteBuf.readResourceLocation();
        int n = friendlyByteBuf.readableBytes();
        if (n < 0 || n > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(n));
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.transactionId);
        friendlyByteBuf.writeResourceLocation(this.identifier);
        friendlyByteBuf.writeBytes(this.data.copy());
    }

    @Override
    public void handle(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.handleCustomQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }
}

