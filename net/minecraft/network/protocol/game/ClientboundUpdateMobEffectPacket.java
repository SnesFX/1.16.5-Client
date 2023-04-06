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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket
implements Packet<ClientGamePacketListener> {
    private int entityId;
    private byte effectId;
    private byte effectAmplifier;
    private int effectDurationTicks;
    private byte flags;

    public ClientboundUpdateMobEffectPacket() {
    }

    public ClientboundUpdateMobEffectPacket(int n, MobEffectInstance mobEffectInstance) {
        this.entityId = n;
        this.effectId = (byte)(MobEffect.getId(mobEffectInstance.getEffect()) & 0xFF);
        this.effectAmplifier = (byte)(mobEffectInstance.getAmplifier() & 0xFF);
        this.effectDurationTicks = mobEffectInstance.getDuration() > 32767 ? 32767 : mobEffectInstance.getDuration();
        this.flags = 0;
        if (mobEffectInstance.isAmbient()) {
            this.flags = (byte)(this.flags | 1);
        }
        if (mobEffectInstance.isVisible()) {
            this.flags = (byte)(this.flags | 2);
        }
        if (mobEffectInstance.showIcon()) {
            this.flags = (byte)(this.flags | 4);
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.entityId = friendlyByteBuf.readVarInt();
        this.effectId = friendlyByteBuf.readByte();
        this.effectAmplifier = friendlyByteBuf.readByte();
        this.effectDurationTicks = friendlyByteBuf.readVarInt();
        this.flags = friendlyByteBuf.readByte();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.entityId);
        friendlyByteBuf.writeByte(this.effectId);
        friendlyByteBuf.writeByte(this.effectAmplifier);
        friendlyByteBuf.writeVarInt(this.effectDurationTicks);
        friendlyByteBuf.writeByte(this.flags);
    }

    public boolean isSuperLongDuration() {
        return this.effectDurationTicks == 32767;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleUpdateMobEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getEffectId() {
        return this.effectId;
    }

    public byte getEffectAmplifier() {
        return this.effectAmplifier;
    }

    public int getEffectDurationTicks() {
        return this.effectDurationTicks;
    }

    public boolean isEffectVisible() {
        return (this.flags & 2) == 2;
    }

    public boolean isEffectAmbient() {
        return (this.flags & 1) == 1;
    }

    public boolean effectShowsIcon() {
        return (this.flags & 4) == 4;
    }
}

