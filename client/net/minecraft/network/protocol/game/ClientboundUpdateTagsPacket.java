/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.tags.TagContainer;

public class ClientboundUpdateTagsPacket
implements Packet<ClientGamePacketListener> {
    private TagContainer tags;

    public ClientboundUpdateTagsPacket() {
    }

    public ClientboundUpdateTagsPacket(TagContainer tagContainer) {
        this.tags = tagContainer;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.tags = TagContainer.deserializeFromNetwork(friendlyByteBuf);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.tags.serializeToNetwork(friendlyByteBuf);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleUpdateTags(this);
    }

    public TagContainer getTags() {
        return this.tags;
    }
}

