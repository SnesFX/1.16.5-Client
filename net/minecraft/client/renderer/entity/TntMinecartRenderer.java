/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.state.BlockState;

public class TntMinecartRenderer
extends MinecartRenderer<MinecartTNT> {
    public TntMinecartRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    protected void renderMinecartContents(MinecartTNT minecartTNT, float f, BlockState blockState, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        int n2 = minecartTNT.getFuse();
        if (n2 > -1 && (float)n2 - f + 1.0f < 10.0f) {
            float f2 = 1.0f - ((float)n2 - f + 1.0f) / 10.0f;
            f2 = Mth.clamp(f2, 0.0f, 1.0f);
            f2 *= f2;
            f2 *= f2;
            float f3 = 1.0f + f2 * 0.3f;
            poseStack.scale(f3, f3, f3);
        }
        TntMinecartRenderer.renderWhiteSolidBlock(blockState, poseStack, multiBufferSource, n, n2 > -1 && n2 / 5 % 2 == 0);
    }

    public static void renderWhiteSolidBlock(BlockState blockState, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, boolean bl) {
        int n2 = bl ? OverlayTexture.pack(OverlayTexture.u(1.0f), 10) : OverlayTexture.NO_OVERLAY;
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, poseStack, multiBufferSource, n, n2);
    }
}

