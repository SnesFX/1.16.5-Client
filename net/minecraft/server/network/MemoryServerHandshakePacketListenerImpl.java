/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

public class MemoryServerHandshakePacketListenerImpl
implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final Connection connection;

    public MemoryServerHandshakePacketListenerImpl(MinecraftServer minecraftServer, Connection connection) {
        this.server = minecraftServer;
        this.connection = connection;
    }

    @Override
    public void handleIntention(ClientIntentionPacket clientIntentionPacket) {
        this.connection.setProtocol(clientIntentionPacket.getIntention());
        this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
    }

    @Override
    public void onDisconnect(Component component) {
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}

