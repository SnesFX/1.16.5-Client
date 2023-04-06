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

public class ClientboundSetExperiencePacket
implements Packet<ClientGamePacketListener> {
    private float experienceProgress;
    private int totalExperience;
    private int experienceLevel;

    public ClientboundSetExperiencePacket() {
    }

    public ClientboundSetExperiencePacket(float f, int n, int n2) {
        this.experienceProgress = f;
        this.totalExperience = n;
        this.experienceLevel = n2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.experienceProgress = friendlyByteBuf.readFloat();
        this.experienceLevel = friendlyByteBuf.readVarInt();
        this.totalExperience = friendlyByteBuf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeFloat(this.experienceProgress);
        friendlyByteBuf.writeVarInt(this.experienceLevel);
        friendlyByteBuf.writeVarInt(this.totalExperience);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetExperience(this);
    }

    public float getExperienceProgress() {
        return this.experienceProgress;
    }

    public int getTotalExperience() {
        return this.totalExperience;
    }

    public int getExperienceLevel() {
        return this.experienceLevel;
    }
}

