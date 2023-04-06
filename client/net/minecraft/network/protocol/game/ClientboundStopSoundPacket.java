/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class ClientboundStopSoundPacket
implements Packet<ClientGamePacketListener> {
    private ResourceLocation name;
    private SoundSource source;

    public ClientboundStopSoundPacket() {
    }

    public ClientboundStopSoundPacket(@Nullable ResourceLocation resourceLocation, @Nullable SoundSource soundSource) {
        this.name = resourceLocation;
        this.source = soundSource;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        byte by = friendlyByteBuf.readByte();
        if ((by & 1) > 0) {
            this.source = friendlyByteBuf.readEnum(SoundSource.class);
        }
        if ((by & 2) > 0) {
            this.name = friendlyByteBuf.readResourceLocation();
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        if (this.source != null) {
            if (this.name != null) {
                friendlyByteBuf.writeByte(3);
                friendlyByteBuf.writeEnum(this.source);
                friendlyByteBuf.writeResourceLocation(this.name);
            } else {
                friendlyByteBuf.writeByte(1);
                friendlyByteBuf.writeEnum(this.source);
            }
        } else if (this.name != null) {
            friendlyByteBuf.writeByte(2);
            friendlyByteBuf.writeResourceLocation(this.name);
        } else {
            friendlyByteBuf.writeByte(0);
        }
    }

    @Nullable
    public ResourceLocation getName() {
        return this.name;
    }

    @Nullable
    public SoundSource getSource() {
        return this.source;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleStopSoundEvent(this);
    }
}

