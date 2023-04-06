/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;

public interface ServerStatusPacketListener
extends PacketListener {
    public void handlePingRequest(ServerboundPingRequestPacket var1);

    public void handleStatusRequest(ServerboundStatusRequestPacket var1);
}

