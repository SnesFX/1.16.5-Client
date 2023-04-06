/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket
implements Packet<ClientGamePacketListener> {
    private DimensionType dimensionType;
    private ResourceKey<Level> dimension;
    private long seed;
    private GameType playerGameType;
    private GameType previousPlayerGameType;
    private boolean isDebug;
    private boolean isFlat;
    private boolean keepAllPlayerData;

    public ClientboundRespawnPacket() {
    }

    public ClientboundRespawnPacket(DimensionType dimensionType, ResourceKey<Level> resourceKey, long l, GameType gameType, GameType gameType2, boolean bl, boolean bl2, boolean bl3) {
        this.dimensionType = dimensionType;
        this.dimension = resourceKey;
        this.seed = l;
        this.playerGameType = gameType;
        this.previousPlayerGameType = gameType2;
        this.isDebug = bl;
        this.isFlat = bl2;
        this.keepAllPlayerData = bl3;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleRespawn(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.dimensionType = friendlyByteBuf.readWithCodec(DimensionType.CODEC).get();
        this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, friendlyByteBuf.readResourceLocation());
        this.seed = friendlyByteBuf.readLong();
        this.playerGameType = GameType.byId(friendlyByteBuf.readUnsignedByte());
        this.previousPlayerGameType = GameType.byId(friendlyByteBuf.readUnsignedByte());
        this.isDebug = friendlyByteBuf.readBoolean();
        this.isFlat = friendlyByteBuf.readBoolean();
        this.keepAllPlayerData = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeWithCodec(DimensionType.CODEC, () -> this.dimensionType);
        friendlyByteBuf.writeResourceLocation(this.dimension.location());
        friendlyByteBuf.writeLong(this.seed);
        friendlyByteBuf.writeByte(this.playerGameType.getId());
        friendlyByteBuf.writeByte(this.previousPlayerGameType.getId());
        friendlyByteBuf.writeBoolean(this.isDebug);
        friendlyByteBuf.writeBoolean(this.isFlat);
        friendlyByteBuf.writeBoolean(this.keepAllPlayerData);
    }

    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public long getSeed() {
        return this.seed;
    }

    public GameType getPlayerGameType() {
        return this.playerGameType;
    }

    public GameType getPreviousPlayerGameType() {
        return this.previousPlayerGameType;
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public boolean isFlat() {
        return this.isFlat;
    }

    public boolean shouldKeepAllPlayerData() {
        return this.keepAllPlayerData;
    }
}

