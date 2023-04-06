/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacket
implements Packet<ClientGamePacketListener> {
    private int x;
    private int z;
    private int availableSections;
    private CompoundTag heightmaps;
    @Nullable
    private int[] biomes;
    private byte[] buffer;
    private List<CompoundTag> blockEntitiesTags;
    private boolean fullChunk;

    public ClientboundLevelChunkPacket() {
    }

    public ClientboundLevelChunkPacket(LevelChunk levelChunk, int n) {
        ChunkPos chunkPos = levelChunk.getPos();
        this.x = chunkPos.x;
        this.z = chunkPos.z;
        this.fullChunk = n == 65535;
        this.heightmaps = new CompoundTag();
        for (Map.Entry<Heightmap.Types, Heightmap> entry : levelChunk.getHeightmaps()) {
            if (!entry.getKey().sendToClient()) continue;
            this.heightmaps.put(entry.getKey().getSerializationKey(), new LongArrayTag(entry.getValue().getRawData()));
        }
        if (this.fullChunk) {
            this.biomes = levelChunk.getBiomes().writeBiomes();
        }
        this.buffer = new byte[this.calculateChunkSize(levelChunk, n)];
        this.availableSections = this.extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), levelChunk, n);
        this.blockEntitiesTags = Lists.newArrayList();
        for (Map.Entry<Object, Object> entry : levelChunk.getBlockEntities().entrySet()) {
            BlockPos blockPos = (BlockPos)entry.getKey();
            BlockEntity blockEntity = (BlockEntity)entry.getValue();
            int n2 = blockPos.getY() >> 4;
            if (!this.isFullChunk() && (n & 1 << n2) == 0) continue;
            CompoundTag compoundTag = blockEntity.getUpdateTag();
            this.blockEntitiesTags.add(compoundTag);
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        int n;
        this.x = friendlyByteBuf.readInt();
        this.z = friendlyByteBuf.readInt();
        this.fullChunk = friendlyByteBuf.readBoolean();
        this.availableSections = friendlyByteBuf.readVarInt();
        this.heightmaps = friendlyByteBuf.readNbt();
        if (this.fullChunk) {
            this.biomes = friendlyByteBuf.readVarIntArray(ChunkBiomeContainer.BIOMES_SIZE);
        }
        if ((n = friendlyByteBuf.readVarInt()) > 2097152) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        this.buffer = new byte[n];
        friendlyByteBuf.readBytes(this.buffer);
        int n2 = friendlyByteBuf.readVarInt();
        this.blockEntitiesTags = Lists.newArrayList();
        for (int i = 0; i < n2; ++i) {
            this.blockEntitiesTags.add(friendlyByteBuf.readNbt());
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeInt(this.x);
        friendlyByteBuf.writeInt(this.z);
        friendlyByteBuf.writeBoolean(this.fullChunk);
        friendlyByteBuf.writeVarInt(this.availableSections);
        friendlyByteBuf.writeNbt(this.heightmaps);
        if (this.biomes != null) {
            friendlyByteBuf.writeVarIntArray(this.biomes);
        }
        friendlyByteBuf.writeVarInt(this.buffer.length);
        friendlyByteBuf.writeBytes(this.buffer);
        friendlyByteBuf.writeVarInt(this.blockEntitiesTags.size());
        for (CompoundTag compoundTag : this.blockEntitiesTags) {
            friendlyByteBuf.writeNbt(compoundTag);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleLevelChunk(this);
    }

    public FriendlyByteBuf getReadBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer((byte[])this.buffer));
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf byteBuf = Unpooled.wrappedBuffer((byte[])this.buffer);
        byteBuf.writerIndex(0);
        return byteBuf;
    }

    public int extractChunkData(FriendlyByteBuf friendlyByteBuf, LevelChunk levelChunk, int n) {
        int n2 = 0;
        LevelChunkSection[] arrlevelChunkSection = levelChunk.getSections();
        int n3 = arrlevelChunkSection.length;
        for (int i = 0; i < n3; ++i) {
            LevelChunkSection levelChunkSection = arrlevelChunkSection[i];
            if (levelChunkSection == LevelChunk.EMPTY_SECTION || this.isFullChunk() && levelChunkSection.isEmpty() || (n & 1 << i) == 0) continue;
            n2 |= 1 << i;
            levelChunkSection.write(friendlyByteBuf);
        }
        return n2;
    }

    protected int calculateChunkSize(LevelChunk levelChunk, int n) {
        int n2 = 0;
        LevelChunkSection[] arrlevelChunkSection = levelChunk.getSections();
        int n3 = arrlevelChunkSection.length;
        for (int i = 0; i < n3; ++i) {
            LevelChunkSection levelChunkSection = arrlevelChunkSection[i];
            if (levelChunkSection == LevelChunk.EMPTY_SECTION || this.isFullChunk() && levelChunkSection.isEmpty() || (n & 1 << i) == 0) continue;
            n2 += levelChunkSection.getSerializedSize();
        }
        return n2;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getAvailableSections() {
        return this.availableSections;
    }

    public boolean isFullChunk() {
        return this.fullChunk;
    }

    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }

    public List<CompoundTag> getBlockEntitiesTags() {
        return this.blockEntitiesTags;
    }

    @Nullable
    public int[] getBiomes() {
        return this.biomes;
    }
}

