/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.login;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ClientboundHelloPacket
implements Packet<ClientLoginPacketListener> {
    private String serverId;
    private byte[] publicKey;
    private byte[] nonce;

    public ClientboundHelloPacket() {
    }

    public ClientboundHelloPacket(String string, byte[] arrby, byte[] arrby2) {
        this.serverId = string;
        this.publicKey = arrby;
        this.nonce = arrby2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.serverId = friendlyByteBuf.readUtf(20);
        this.publicKey = friendlyByteBuf.readByteArray();
        this.nonce = friendlyByteBuf.readByteArray();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeUtf(this.serverId);
        friendlyByteBuf.writeByteArray(this.publicKey);
        friendlyByteBuf.writeByteArray(this.nonce);
    }

    @Override
    public void handle(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.handleHello(this);
    }

    public String getServerId() {
        return this.serverId;
    }

    public PublicKey getPublicKey() throws CryptException {
        return Crypt.byteToPublicKey(this.publicKey);
    }

    public byte[] getNonce() {
        return this.nonce;
    }
}

