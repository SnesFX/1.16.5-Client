/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundLoginPacket
implements Packet<ClientGamePacketListener> {
    private int playerId;
    private long seed;
    private boolean hardcore;
    private GameType gameType;
    private GameType previousGameType;
    private Set<ResourceKey<Level>> levels;
    private RegistryAccess.RegistryHolder registryHolder;
    private DimensionType dimensionType;
    private ResourceKey<Level> dimension;
    private int maxPlayers;
    private int chunkRadius;
    private boolean reducedDebugInfo;
    private boolean showDeathScreen;
    private boolean isDebug;
    private boolean isFlat;

    public ClientboundLoginPacket() {
    }

    public ClientboundLoginPacket(int n, GameType gameType, GameType gameType2, long l, boolean bl, Set<ResourceKey<Level>> set, RegistryAccess.RegistryHolder registryHolder, DimensionType dimensionType, ResourceKey<Level> resourceKey, int n2, int n3, boolean bl2, boolean bl3, boolean bl4, boolean bl5) {
        this.playerId = n;
        this.levels = set;
        this.registryHolder = registryHolder;
        this.dimensionType = dimensionType;
        this.dimension = resourceKey;
        this.seed = l;
        this.gameType = gameType;
        this.previousGameType = gameType2;
        this.maxPlayers = n2;
        this.hardcore = bl;
        this.chunkRadius = n3;
        this.reducedDebugInfo = bl2;
        this.showDeathScreen = bl3;
        this.isDebug = bl4;
        this.isFlat = bl5;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.playerId = friendlyByteBuf.readInt();
        this.hardcore = friendlyByteBuf.readBoolean();
        this.gameType = GameType.byId(friendlyByteBuf.readByte());
        this.previousGameType = GameType.byId(friendlyByteBuf.readByte());
        int n = friendlyByteBuf.readVarInt();
        this.levels = Sets.newHashSet();
        for (int i = 0; i < n; ++i) {
            this.levels.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, friendlyByteBuf.readResourceLocation()));
        }
        this.registryHolder = friendlyByteBuf.readWithCodec(RegistryAccess.RegistryHolder.NETWORK_CODEC);
        this.dimensionType = friendlyByteBuf.readWithCodec(DimensionType.CODEC).get();
        this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, friendlyByteBuf.readResourceLocation());
        this.seed = friendlyByteBuf.readLong();
        this.maxPlayers = friendlyByteBuf.readVarInt();
        this.chunkRadius = friendlyByteBuf.readVarInt();
        this.reducedDebugInfo = friendlyByteBuf.readBoolean();
        this.showDeathScreen = friendlyByteBuf.readBoolean();
        this.isDebug = friendlyByteBuf.readBoolean();
        this.isFlat = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeInt(this.playerId);
        friendlyByteBuf.writeBoolean(this.hardcore);
        friendlyByteBuf.writeByte(this.gameType.getId());
        friendlyByteBuf.writeByte(this.previousGameType.getId());
        friendlyByteBuf.writeVarInt(this.levels.size());
        for (ResourceKey<Level> resourceKey : this.levels) {
            friendlyByteBuf.writeResourceLocation(resourceKey.location());
        }
        friendlyByteBuf.writeWithCodec(RegistryAccess.RegistryHolder.NETWORK_CODEC, this.registryHolder);
        friendlyByteBuf.writeWithCodec(DimensionType.CODEC, () -> this.dimensionType);
        friendlyByteBuf.writeResourceLocation(this.dimension.location());
        friendlyByteBuf.writeLong(this.seed);
        friendlyByteBuf.writeVarInt(this.maxPlayers);
        friendlyByteBuf.writeVarInt(this.chunkRadius);
        friendlyByteBuf.writeBoolean(this.reducedDebugInfo);
        friendlyByteBuf.writeBoolean(this.showDeathScreen);
        friendlyByteBuf.writeBoolean(this.isDebug);
        friendlyByteBuf.writeBoolean(this.isFlat);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleLogin(this);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public GameType getPreviousGameType() {
        return this.previousGameType;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public RegistryAccess registryAccess() {
        return this.registryHolder;
    }

    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public int getChunkRadius() {
        return this.chunkRadius;
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public boolean shouldShowDeathScreen() {
        return this.showDeathScreen;
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public boolean isFlat() {
        return this.isFlat;
    }
}

