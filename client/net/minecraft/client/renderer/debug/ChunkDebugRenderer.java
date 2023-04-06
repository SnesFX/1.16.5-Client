/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class ChunkDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private double lastUpdateTime = Double.MIN_VALUE;
    private final int radius = 12;
    @Nullable
    private ChunkData data;

    public ChunkDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        Object object;
        double d4 = Util.getNanos();
        if (d4 - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = d4;
            object = this.minecraft.getSingleplayerServer();
            this.data = object != null ? new ChunkData((IntegratedServer)object, d, d3) : null;
        }
        if (this.data != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(2.0f);
            RenderSystem.disableTexture();
            RenderSystem.depthMask(false);
            object = this.data.serverData.getNow(null);
            double d5 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85;
            for (Map.Entry entry : this.data.clientData.entrySet()) {
                ChunkPos chunkPos = (ChunkPos)entry.getKey();
                String string = (String)entry.getValue();
                if (object != null) {
                    string = string + (String)object.get(chunkPos);
                }
                String[] arrstring = string.split("\n");
                int n = 0;
                for (String string2 : arrstring) {
                    DebugRenderer.renderFloatingText(string2, (chunkPos.x << 4) + 8, d5 + (double)n, (chunkPos.z << 4) + 8, -1, 0.15f);
                    n -= 2;
                }
            }
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    static /* synthetic */ Minecraft access$300(ChunkDebugRenderer chunkDebugRenderer) {
        return chunkDebugRenderer.minecraft;
    }

    final class ChunkData {
        private final Map<ChunkPos, String> clientData;
        private final CompletableFuture<Map<ChunkPos, String>> serverData;

        private ChunkData(IntegratedServer integratedServer, double d, double d2) {
            ClientLevel clientLevel = ChunkDebugRenderer.access$300((ChunkDebugRenderer)ChunkDebugRenderer.this).level;
            ResourceKey<Level> resourceKey = clientLevel.dimension();
            int n = (int)d >> 4;
            int n2 = (int)d2 >> 4;
            ImmutableMap.Builder builder = ImmutableMap.builder();
            ClientChunkCache clientChunkCache = clientLevel.getChunkSource();
            for (int i = n - 12; i <= n + 12; ++i) {
                for (int j = n2 - 12; j <= n2 + 12; ++j) {
                    ChunkPos chunkPos = new ChunkPos(i, j);
                    String string = "";
                    LevelChunk levelChunk = clientChunkCache.getChunk(i, j, false);
                    string = string + "Client: ";
                    if (levelChunk == null) {
                        string = string + "0n/a\n";
                    } else {
                        string = string + (levelChunk.isEmpty() ? " E" : "");
                        string = string + "\n";
                    }
                    builder.put((Object)chunkPos, (Object)string);
                }
            }
            this.clientData = builder.build();
            this.serverData = integratedServer.submit(() -> {
                ServerLevel serverLevel = integratedServer.getLevel(resourceKey);
                if (serverLevel == null) {
                    return ImmutableMap.of();
                }
                ImmutableMap.Builder builder = ImmutableMap.builder();
                ServerChunkCache serverChunkCache = serverLevel.getChunkSource();
                for (int i = n - 12; i <= n + 12; ++i) {
                    for (int j = n2 - 12; j <= n2 + 12; ++j) {
                        ChunkPos chunkPos = new ChunkPos(i, j);
                        builder.put((Object)chunkPos, (Object)("Server: " + serverChunkCache.getChunkDebugData(chunkPos)));
                    }
                }
                return builder.build();
            });
        }
    }

}

