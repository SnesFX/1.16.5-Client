/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.Item;

public class ClientboundCooldownPacket
implements Packet<ClientGamePacketListener> {
    private Item item;
    private int duration;

    public ClientboundCooldownPacket() {
    }

    public ClientboundCooldownPacket(Item item, int n) {
        this.item = item;
        this.duration = n;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.item = Item.byId(friendlyByteBuf.readVarInt());
        this.duration = friendlyByteBuf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(Item.getId(this.item));
        friendlyByteBuf.writeVarInt(this.duration);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleItemCooldown(this);
    }

    public Item getItem() {
        return this.item;
    }

    public int getDuration() {
        return this.duration;
    }
}

