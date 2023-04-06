/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang3.Validate;

public class ClientboundSoundPacket
implements Packet<ClientGamePacketListener> {
    private SoundEvent sound;
    private SoundSource source;
    private int x;
    private int y;
    private int z;
    private float volume;
    private float pitch;

    public ClientboundSoundPacket() {
    }

    public ClientboundSoundPacket(SoundEvent soundEvent, SoundSource soundSource, double d, double d2, double d3, float f, float f2) {
        Validate.notNull((Object)soundEvent, (String)"sound", (Object[])new Object[0]);
        this.sound = soundEvent;
        this.source = soundSource;
        this.x = (int)(d * 8.0);
        this.y = (int)(d2 * 8.0);
        this.z = (int)(d3 * 8.0);
        this.volume = f;
        this.pitch = f2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.sound = (SoundEvent)Registry.SOUND_EVENT.byId(friendlyByteBuf.readVarInt());
        this.source = friendlyByteBuf.readEnum(SoundSource.class);
        this.x = friendlyByteBuf.readInt();
        this.y = friendlyByteBuf.readInt();
        this.z = friendlyByteBuf.readInt();
        this.volume = friendlyByteBuf.readFloat();
        this.pitch = friendlyByteBuf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
        friendlyByteBuf.writeEnum(this.source);
        friendlyByteBuf.writeInt(this.x);
        friendlyByteBuf.writeInt(this.y);
        friendlyByteBuf.writeInt(this.z);
        friendlyByteBuf.writeFloat(this.volume);
        friendlyByteBuf.writeFloat(this.pitch);
    }

    public SoundEvent getSound() {
        return this.sound;
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
        clientGamePacketListener.handleSoundEvent(this);
    }
}

