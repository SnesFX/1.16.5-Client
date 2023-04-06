/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;

public class LevelLoadingScreen
extends Screen {
    private final StoringChunkProgressListener progressListener;
    private long lastNarration = -1L;
    private static final Object2IntMap<ChunkStatus> COLORS = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
        object2IntOpenHashMap.defaultReturnValue(0);
        object2IntOpenHashMap.put((Object)ChunkStatus.EMPTY, 5526612);
        object2IntOpenHashMap.put((Object)ChunkStatus.STRUCTURE_STARTS, 10066329);
        object2IntOpenHashMap.put((Object)ChunkStatus.STRUCTURE_REFERENCES, 6250897);
        object2IntOpenHashMap.put((Object)ChunkStatus.BIOMES, 8434258);
        object2IntOpenHashMap.put((Object)ChunkStatus.NOISE, 13750737);
        object2IntOpenHashMap.put((Object)ChunkStatus.SURFACE, 7497737);
        object2IntOpenHashMap.put((Object)ChunkStatus.CARVERS, 7169628);
        object2IntOpenHashMap.put((Object)ChunkStatus.LIQUID_CARVERS, 3159410);
        object2IntOpenHashMap.put((Object)ChunkStatus.FEATURES, 2213376);
        object2IntOpenHashMap.put((Object)ChunkStatus.LIGHT, 13421772);
        object2IntOpenHashMap.put((Object)ChunkStatus.SPAWN, 15884384);
        object2IntOpenHashMap.put((Object)ChunkStatus.HEIGHTMAPS, 15658734);
        object2IntOpenHashMap.put((Object)ChunkStatus.FULL, 16777215);
    });

    public LevelLoadingScreen(StoringChunkProgressListener storingChunkProgressListener) {
        super(NarratorChatListener.NO_TITLE);
        this.progressListener = storingChunkProgressListener;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void removed() {
        NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.loading.done").getString());
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        String string = Mth.clamp(this.progressListener.getProgress(), 0, 100) + "%";
        long l = Util.getMillis();
        if (l - this.lastNarration > 2000L) {
            this.lastNarration = l;
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.loading", string).getString());
        }
        int n3 = this.width / 2;
        int n4 = this.height / 2;
        int n5 = 30;
        LevelLoadingScreen.renderChunks(poseStack, this.progressListener, n3, n4 + 30, 2, 0);
        this.font.getClass();
        LevelLoadingScreen.drawCenteredString(poseStack, this.font, string, n3, n4 - 9 / 2 - 30, 16777215);
    }

    public static void renderChunks(PoseStack poseStack, StoringChunkProgressListener storingChunkProgressListener, int n, int n2, int n3, int n4) {
        int n5 = n3 + n4;
        int n6 = storingChunkProgressListener.getFullDiameter();
        int n7 = n6 * n5 - n4;
        int n8 = storingChunkProgressListener.getDiameter();
        int n9 = n8 * n5 - n4;
        int n10 = n - n9 / 2;
        int n11 = n2 - n9 / 2;
        int n12 = n7 / 2 + 1;
        int n13 = -16772609;
        if (n4 != 0) {
            LevelLoadingScreen.fill(poseStack, n - n12, n2 - n12, n - n12 + 1, n2 + n12, -16772609);
            LevelLoadingScreen.fill(poseStack, n + n12 - 1, n2 - n12, n + n12, n2 + n12, -16772609);
            LevelLoadingScreen.fill(poseStack, n - n12, n2 - n12, n + n12, n2 - n12 + 1, -16772609);
            LevelLoadingScreen.fill(poseStack, n - n12, n2 + n12 - 1, n + n12, n2 + n12, -16772609);
        }
        for (int i = 0; i < n8; ++i) {
            for (int j = 0; j < n8; ++j) {
                ChunkStatus chunkStatus = storingChunkProgressListener.getStatus(i, j);
                int n14 = n10 + i * n5;
                int n15 = n11 + j * n5;
                LevelLoadingScreen.fill(poseStack, n14, n15, n14 + n3, n15 + n3, COLORS.getInt((Object)chunkStatus) | 0xFF000000);
            }
        }
    }
}

