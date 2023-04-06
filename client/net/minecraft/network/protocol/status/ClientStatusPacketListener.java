/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;

public interface ClientStatusPacketListener
extends PacketListener {
    public void handleStatusResponse(ClientboundStatusResponsePacket var1);

    public void handlePongResponse(ClientboundPongResponsePacket var1);
}

