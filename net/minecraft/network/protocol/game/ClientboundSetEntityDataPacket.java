/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;

public class ClientboundSetEntityDataPacket
implements Packet<ClientGamePacketListener> {
    private int id;
    private List<SynchedEntityData.DataItem<?>> packedItems;

    public ClientboundSetEntityDataPacket() {
    }

    public ClientboundSetEntityDataPacket(int n, SynchedEntityData synchedEntityData, boolean bl) {
        this.id = n;
        if (bl) {
            this.packedItems = synchedEntityData.getAll();
            synchedEntityData.clearDirty();
        } else {
            this.packedItems = synchedEntityData.packDirty();
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.id = friendlyByteBuf.readVarInt();
        this.packedItems = SynchedEntityData.unpack(friendlyByteBuf);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.id);
        SynchedEntityData.pack(this.packedItems, friendlyByteBuf);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetEntityData(this);
    }

    public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
        return this.packedItems;
    }

    public int getId() {
        return this.id;
    }
}

