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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRemoveMobEffectPacket
implements Packet<ClientGamePacketListener> {
    private int entityId;
    private MobEffect effect;

    public ClientboundRemoveMobEffectPacket() {
    }

    public ClientboundRemoveMobEffectPacket(int n, MobEffect mobEffect) {
        this.entityId = n;
        this.effect = mobEffect;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.entityId = friendlyByteBuf.readVarInt();
        this.effect = MobEffect.byId(friendlyByteBuf.readUnsignedByte());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.entityId);
        friendlyByteBuf.writeByte(MobEffect.getId(this.effect));
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleRemoveMobEffect(this);
    }

    @Nullable
    public Entity getEntity(Level level) {
        return level.getEntity(this.entityId);
    }

    @Nullable
    public MobEffect getEffect() {
        return this.effect;
    }
}

