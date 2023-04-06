/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundTagQueryPacket
implements Packet<ClientGamePacketListener> {
    private int transactionId;
    @Nullable
    private CompoundTag tag;

    public ClientboundTagQueryPacket() {
    }

    public ClientboundTagQueryPacket(int n, @Nullable CompoundTag compoundTag) {
        this.transactionId = n;
        this.tag = compoundTag;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.transactionId = friendlyByteBuf.readVarInt();
        this.tag = friendlyByteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.transactionId);
        friendlyByteBuf.writeNbt(this.tag);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleTagQueryPacket(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}

