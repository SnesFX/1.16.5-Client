/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketEncoder
extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker((String)"PACKET_SENT", (Marker)Connection.PACKET_MARKER);
    private final PacketFlow flow;

    public PacketEncoder(PacketFlow packetFlow) {
        this.flow = packetFlow;
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
        ConnectionProtocol connectionProtocol = (ConnectionProtocol)((Object)channelHandlerContext.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get());
        if (connectionProtocol == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + packet);
        }
        Integer n = connectionProtocol.getPacketId(this.flow, packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), (Object)n, (Object)packet.getClass().getName());
        }
        if (n == null) {
            throw new IOException("Can't serialize unregistered packet");
        }
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(byteBuf);
        friendlyByteBuf.writeVarInt(n);
        try {
            packet.write(friendlyByteBuf);
        }
        catch (Throwable throwable) {
            LOGGER.error((Object)throwable);
            if (packet.isSkippable()) {
                throw new SkipPacketException(throwable);
            }
            throw throwable;
        }
    }

    protected /* synthetic */ void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        this.encode(channelHandlerContext, (Packet)object, byteBuf);
    }
}

