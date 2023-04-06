/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket
implements Packet<ClientGamePacketListener> {
    private int x;
    private int z;
    private int skyYMask;
    private int blockYMask;
    private int emptySkyYMask;
    private int emptyBlockYMask;
    private List<byte[]> skyUpdates;
    private List<byte[]> blockUpdates;
    private boolean trustEdges;

    public ClientboundLightUpdatePacket() {
    }

    public ClientboundLightUpdatePacket(ChunkPos chunkPos, LevelLightEngine levelLightEngine, boolean bl) {
        this.x = chunkPos.x;
        this.z = chunkPos.z;
        this.trustEdges = bl;
        this.skyUpdates = Lists.newArrayList();
        this.blockUpdates = Lists.newArrayList();
        for (int i = 0; i < 18; ++i) {
            DataLayer dataLayer = levelLightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(chunkPos, -1 + i));
            DataLayer dataLayer2 = levelLightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(chunkPos, -1 + i));
            if (dataLayer != null) {
                if (dataLayer.isEmpty()) {
                    this.emptySkyYMask |= 1 << i;
                } else {
                    this.skyYMask |= 1 << i;
                    this.skyUpdates.add((byte[])dataLayer.getData().clone());
                }
            }
            if (dataLayer2 == null) continue;
            if (dataLayer2.isEmpty()) {
                this.emptyBlockYMask |= 1 << i;
                continue;
            }
            this.blockYMask |= 1 << i;
            this.blockUpdates.add((byte[])dataLayer2.getData().clone());
        }
    }

    public ClientboundLightUpdatePacket(ChunkPos chunkPos, LevelLightEngine levelLightEngine, int n, int n2, boolean bl) {
        this.x = chunkPos.x;
        this.z = chunkPos.z;
        this.trustEdges = bl;
        this.skyYMask = n;
        this.blockYMask = n2;
        this.skyUpdates = Lists.newArrayList();
        this.blockUpdates = Lists.newArrayList();
        for (int i = 0; i < 18; ++i) {
            DataLayer dataLayer;
            if ((this.skyYMask & 1 << i) != 0) {
                dataLayer = levelLightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(chunkPos, -1 + i));
                if (dataLayer == null || dataLayer.isEmpty()) {
                    this.skyYMask &= ~(1 << i);
                    if (dataLayer != null) {
                        this.emptySkyYMask |= 1 << i;
                    }
                } else {
                    this.skyUpdates.add((byte[])dataLayer.getData().clone());
                }
            }
            if ((this.blockYMask & 1 << i) == 0) continue;
            dataLayer = levelLightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(chunkPos, -1 + i));
            if (dataLayer == null || dataLayer.isEmpty()) {
                this.blockYMask &= ~(1 << i);
                if (dataLayer == null) continue;
                this.emptyBlockYMask |= 1 << i;
                continue;
            }
            this.blockUpdates.add((byte[])dataLayer.getData().clone());
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        int n;
        this.x = friendlyByteBuf.readVarInt();
        this.z = friendlyByteBuf.readVarInt();
        this.trustEdges = friendlyByteBuf.readBoolean();
        this.skyYMask = friendlyByteBuf.readVarInt();
        this.blockYMask = friendlyByteBuf.readVarInt();
        this.emptySkyYMask = friendlyByteBuf.readVarInt();
        this.emptyBlockYMask = friendlyByteBuf.readVarInt();
        this.skyUpdates = Lists.newArrayList();
        for (n = 0; n < 18; ++n) {
            if ((this.skyYMask & 1 << n) == 0) continue;
            this.skyUpdates.add(friendlyByteBuf.readByteArray(2048));
        }
        this.blockUpdates = Lists.newArrayList();
        for (n = 0; n < 18; ++n) {
            if ((this.blockYMask & 1 << n) == 0) continue;
            this.blockUpdates.add(friendlyByteBuf.readByteArray(2048));
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.x);
        friendlyByteBuf.writeVarInt(this.z);
        friendlyByteBuf.writeBoolean(this.trustEdges);
        friendlyByteBuf.writeVarInt(this.skyYMask);
        friendlyByteBuf.writeVarInt(this.blockYMask);
        friendlyByteBuf.writeVarInt(this.emptySkyYMask);
        friendlyByteBuf.writeVarInt(this.emptyBlockYMask);
        for (byte[] arrby : this.skyUpdates) {
            friendlyByteBuf.writeByteArray(arrby);
        }
        for (byte[] arrby : this.blockUpdates) {
            friendlyByteBuf.writeByteArray(arrby);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleLightUpdatePacked(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getSkyYMask() {
        return this.skyYMask;
    }

    public int getEmptySkyYMask() {
        return this.emptySkyYMask;
    }

    public List<byte[]> getSkyUpdates() {
        return this.skyUpdates;
    }

    public int getBlockYMask() {
        return this.blockYMask;
    }

    public int getEmptyBlockYMask() {
        return this.emptyBlockYMask;
    }

    public List<byte[]> getBlockUpdates() {
        return this.blockUpdates;
    }

    public boolean getTrustEdges() {
        return this.trustEdges;
    }
}

