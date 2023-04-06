/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundLoginDisconnectPacket
implements Packet<ClientLoginPacketListener> {
    private Component reason;

    public ClientboundLoginDisconnectPacket() {
    }

    public ClientboundLoginDisconnectPacket(Component component) {
        this.reason = component;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.reason = Component.Serializer.fromJsonLenient(friendlyByteBuf.readUtf(262144));
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeComponent(this.reason);
    }

    @Override
    public void handle(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.handleDisconnect(this);
    }

    public Component getReason() {
        return this.reason;
    }
}

