/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundResourcePackPacket
implements Packet<ClientGamePacketListener> {
    private String url;
    private String hash;

    public ClientboundResourcePackPacket() {
    }

    public ClientboundResourcePackPacket(String string, String string2) {
        this.url = string;
        this.hash = string2;
        if (string2.length() > 40) {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + string2.length() + ")");
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.url = friendlyByteBuf.readUtf(32767);
        this.hash = friendlyByteBuf.readUtf(40);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeUtf(this.url);
        friendlyByteBuf.writeUtf(this.hash);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleResourcePack(this);
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }
}

