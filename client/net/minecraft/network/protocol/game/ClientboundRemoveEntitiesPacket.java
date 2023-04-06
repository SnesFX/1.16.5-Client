/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundRemoveEntitiesPacket
implements Packet<ClientGamePacketListener> {
    private int[] entityIds;

    public ClientboundRemoveEntitiesPacket() {
    }

    public ClientboundRemoveEntitiesPacket(int ... arrn) {
        this.entityIds = arrn;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.entityIds = new int[friendlyByteBuf.readVarInt()];
        for (int i = 0; i < this.entityIds.length; ++i) {
            this.entityIds[i] = friendlyByteBuf.readVarInt();
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.entityIds.length);
        for (int n : this.entityIds) {
            friendlyByteBuf.writeVarInt(n);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleRemoveEntity(this);
    }

    public int[] getEntityIds() {
        return this.entityIds;
    }
}

