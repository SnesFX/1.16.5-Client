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
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.item.ItemStack;

public class ServerboundSetCreativeModeSlotPacket
implements Packet<ServerGamePacketListener> {
    private int slotNum;
    private ItemStack itemStack = ItemStack.EMPTY;

    public ServerboundSetCreativeModeSlotPacket() {
    }

    public ServerboundSetCreativeModeSlotPacket(int n, ItemStack itemStack) {
        this.slotNum = n;
        this.itemStack = itemStack.copy();
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleSetCreativeModeSlot(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.slotNum = friendlyByteBuf.readShort();
        this.itemStack = friendlyByteBuf.readItem();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeShort(this.slotNum);
        friendlyByteBuf.writeItem(this.itemStack);
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}

