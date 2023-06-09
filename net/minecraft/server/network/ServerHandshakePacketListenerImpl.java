/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;

public class ServerHandshakePacketListenerImpl
implements ServerHandshakePacketListener {
    private static final Component IGNORE_STATUS_REASON = new TextComponent("Ignoring status request");
    private final MinecraftServer server;
    private final Connection connection;

    public ServerHandshakePacketListenerImpl(MinecraftServer minecraftServer, Connection connection) {
        this.server = minecraftServer;
        this.connection = connection;
    }

    @Override
    public void handleIntention(ClientIntentionPacket clientIntentionPacket) {
        switch (clientIntentionPacket.getIntention()) {
            case LOGIN: {
                this.connection.setProtocol(ConnectionProtocol.LOGIN);
                if (clientIntentionPacket.getProtocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
                    TranslatableComponent translatableComponent = clientIntentionPacket.getProtocolVersion() < 754 ? new TranslatableComponent("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName()) : new TranslatableComponent("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
                    this.connection.send(new ClientboundLoginDisconnectPacket(translatableComponent));
                    this.connection.disconnect(translatableComponent);
                    break;
                }
                this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
                break;
            }
            case STATUS: {
                if (this.server.repliesToStatus()) {
                    this.connection.setProtocol(ConnectionProtocol.STATUS);
                    this.connection.setListener(new ServerStatusPacketListenerImpl(this.server, this.connection));
                    break;
                }
                this.connection.disconnect(IGNORE_STATUS_REASON);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Invalid intention " + (Object)((Object)clientIntentionPacket.getIntention()));
            }
        }
    }

    @Override
    public void onDisconnect(Component component) {
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

}

