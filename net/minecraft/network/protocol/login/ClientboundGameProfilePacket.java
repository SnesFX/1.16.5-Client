/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.SerializableUUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundGameProfilePacket
implements Packet<ClientLoginPacketListener> {
    private GameProfile gameProfile;

    public ClientboundGameProfilePacket() {
    }

    public ClientboundGameProfilePacket(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        int[] arrn = new int[4];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = friendlyByteBuf.readInt();
        }
        UUID uUID = SerializableUUID.uuidFromIntArray(arrn);
        String string = friendlyByteBuf.readUtf(16);
        this.gameProfile = new GameProfile(uUID, string);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        for (int n : SerializableUUID.uuidToIntArray(this.gameProfile.getId())) {
            friendlyByteBuf.writeInt(n);
        }
        friendlyByteBuf.writeUtf(this.gameProfile.getName());
    }

    @Override
    public void handle(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.handleGameProfile(this);
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
}

