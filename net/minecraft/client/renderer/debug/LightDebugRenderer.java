/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class LightDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public LightDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        ClientLevel clientLevel = this.minecraft.level;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(d, d2, d3);
        LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-10, -10, -10), blockPos.offset(10, 10, 10))) {
            int n = clientLevel.getBrightness(LightLayer.SKY, blockPos2);
            float f = (float)(15 - n) / 15.0f * 0.5f + 0.16f;
            int n2 = Mth.hsvToRgb(f, 0.9f, 0.9f);
            long l = SectionPos.blockToSection(blockPos2.asLong());
            if (longOpenHashSet.add(l)) {
                DebugRenderer.renderFloatingText(clientLevel.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(l)), SectionPos.x(l) * 16 + 8, SectionPos.y(l) * 16 + 8, SectionPos.z(l) * 16 + 8, 16711680, 0.3f);
            }
            if (n == 15) continue;
            DebugRenderer.renderFloatingText(String.valueOf(n), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, n2);
        }
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

