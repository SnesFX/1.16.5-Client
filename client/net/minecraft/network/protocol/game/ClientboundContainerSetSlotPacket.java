/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotPacket
implements Packet<ClientGamePacketListener> {
    private int containerId;
    private int slot;
    private ItemStack itemStack = ItemStack.EMPTY;

    public ClientboundContainerSetSlotPacket() {
    }

    public ClientboundContainerSetSlotPacket(int n, int n2, ItemStack itemStack) {
        this.containerId = n;
        this.slot = n2;
        this.itemStack = itemStack.copy();
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleContainerSetSlot(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readByte();
        this.slot = friendlyByteBuf.readShort();
        this.itemStack = friendlyByteBuf.readItem();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.containerId);
        friendlyByteBuf.writeShort(this.slot);
        friendlyByteBuf.writeItem(this.itemStack);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}

