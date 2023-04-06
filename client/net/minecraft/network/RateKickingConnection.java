/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RateKickingConnection
extends Connection {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component EXCEED_REASON = new TranslatableComponent("disconnect.exceeded_packet_rate");
    private final int rateLimitPacketsPerSecond;

    public RateKickingConnection(int n) {
        super(PacketFlow.SERVERBOUND);
        this.rateLimitPacketsPerSecond = n;
    }

    @Override
    protected void tickSecond() {
        super.tickSecond();
        float f = this.getAverageReceivedPackets();
        if (f > (float)this.rateLimitPacketsPerSecond) {
            LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", (Object)Float.valueOf(f));
            this.send(new ClientboundDisconnectPacket(EXCEED_REASON), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.disconnect(EXCEED_REASON)));
            this.setReadOnly();
        }
    }
}

