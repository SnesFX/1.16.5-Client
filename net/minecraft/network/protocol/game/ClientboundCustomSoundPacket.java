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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class ClientboundCustomSoundPacket
implements Packet<ClientGamePacketListener> {
    private ResourceLocation name;
    private SoundSource source;
    private int x;
    private int y = Integer.MAX_VALUE;
    private int z;
    private float volume;
    private float pitch;

    public ClientboundCustomSoundPacket() {
    }

    public ClientboundCustomSoundPacket(ResourceLocation resourceLocation, SoundSource soundSource, Vec3 vec3, float f, float f2) {
        this.name = resourceLocation;
        this.source = soundSource;
        this.x = (int)(vec3.x * 8.0);
        this.y = (int)(vec3.y * 8.0);
        this.z = (int)(vec3.z * 8.0);
        this.volume = f;
        this.pitch = f2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.name = friendlyByteBuf.readResourceLocation();
        this.source = friendlyByteBuf.readEnum(SoundSource.class);
        this.x = friendlyByteBuf.readInt();
        this.y = friendlyByteBuf.readInt();
        this.z = friendlyByteBuf.readInt();
        this.volume = friendlyByteBuf.readFloat();
        this.pitch = friendlyByteBuf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeResourceLocation(this.name);
        friendlyByteBuf.writeEnum(this.source);
        friendlyByteBuf.writeInt(this.x);
        friendlyByteBuf.writeInt(this.y);
        friendlyByteBuf.writeInt(this.z);
        friendlyByteBuf.writeFloat(this.volume);
        friendlyByteBuf.writeFloat(this.pitch);
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public SoundSource getSource() {
        return this.source;
    }

    public double getX() {
        return (float)this.x / 8.0f;
    }

    public double getY() {
        return (float)this.y / 8.0f;
    }

    public double getZ() {
        return (float)this.z / 8.0f;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleCustomSoundEvent(this);
    }
}

