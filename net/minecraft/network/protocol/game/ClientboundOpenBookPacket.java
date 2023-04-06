/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;

public class ClientboundOpenBookPacket
implements Packet<ClientGamePacketListener> {
    private InteractionHand hand;

    public ClientboundOpenBookPacket() {
    }

    public ClientboundOpenBookPacket(InteractionHand interactionHand) {
        this.hand = interactionHand;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.hand = friendlyByteBuf.readEnum(InteractionHand.class);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.hand);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleOpenBook(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }
}

