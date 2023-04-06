/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
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
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketDecoder
extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker((String)"PACKET_RECEIVED", (Marker)Connection.PACKET_MARKER);
    private final PacketFlow flow;

    public PacketDecoder(PacketFlow packetFlow) {
        this.flow = packetFlow;
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(byteBuf);
        int n = friendlyByteBuf.readVarInt();
        Packet<?> packet = ((ConnectionProtocol)((Object)channelHandlerContext.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get())).createPacket(this.flow, n);
        if (packet == null) {
            throw new IOException("Bad packet id " + n);
        }
        packet.read(friendlyByteBuf);
        if (friendlyByteBuf.readableBytes() > 0) {
            throw new IOException("Packet " + ((ConnectionProtocol)((Object)channelHandlerContext.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get())).getId() + "/" + n + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + friendlyByteBuf.readableBytes() + " bytes extra whilst reading packet " + n);
        }
        list.add(packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MARKER, " IN: [{}:{}] {}", channelHandlerContext.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), (Object)n, (Object)packet.getClass().getName());
        }
    }
}

