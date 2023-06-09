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

public class ServerboundEditBookPacket
implements Packet<ServerGamePacketListener> {
    private ItemStack book;
    private boolean signing;
    private int slot;

    public ServerboundEditBookPacket() {
    }

    public ServerboundEditBookPacket(ItemStack itemStack, boolean bl, int n) {
        this.book = itemStack.copy();
        this.signing = bl;
        this.slot = n;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.book = friendlyByteBuf.readItem();
        this.signing = friendlyByteBuf.readBoolean();
        this.slot = friendlyByteBuf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeItem(this.book);
        friendlyByteBuf.writeBoolean(this.signing);
        friendlyByteBuf.writeVarInt(this.slot);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleEditBook(this);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean isSigning() {
        return this.signing;
    }

    public int getSlot() {
        return this.slot;
    }
}

