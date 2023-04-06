/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetContentPacket
implements Packet<ClientGamePacketListener> {
    private int containerId;
    private List<ItemStack> items;

    public ClientboundContainerSetContentPacket() {
    }

    public ClientboundContainerSetContentPacket(int n, NonNullList<ItemStack> nonNullList) {
        this.containerId = n;
        this.items = NonNullList.withSize(nonNullList.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.items.size(); ++i) {
            this.items.set(i, nonNullList.get(i).copy());
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readUnsignedByte();
        int n = friendlyByteBuf.readShort();
        this.items = NonNullList.withSize(n, ItemStack.EMPTY);
        for (int i = 0; i < n; ++i) {
            this.items.set(i, friendlyByteBuf.readItem());
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.containerId);
        friendlyByteBuf.writeShort(this.items.size());
        for (ItemStack itemStack : this.items) {
            friendlyByteBuf.writeItem(itemStack);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleContainerContent(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }
}

