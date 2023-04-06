/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class ScreenEffectRenderer {
    private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");

    public static void renderScreenEffect(Minecraft minecraft, PoseStack poseStack) {
        BlockState blockState;
        RenderSystem.disableAlphaTest();
        LocalPlayer localPlayer = minecraft.player;
        if (!localPlayer.noPhysics && (blockState = ScreenEffectRenderer.getViewBlockingState(localPlayer)) != null) {
            ScreenEffectRenderer.renderTex(minecraft, minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(blockState), poseStack);
        }
        if (!minecraft.player.isSpectator()) {
            if (minecraft.player.isEyeInFluid(FluidTags.WATER)) {
                ScreenEffectRenderer.renderWater(minecraft, poseStack);
            }
            if (minecraft.player.isOnFire()) {
                ScreenEffectRenderer.renderFire(minecraft, poseStack);
            }
        }
        RenderSystem.enableAlphaTest();
    }

    @Nullable
    private static BlockState getViewBlockingState(Player player) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 8; ++i) {
            double d = player.getX() + (double)(((float)((i >> 0) % 2) - 0.5f) * player.getBbWidth() * 0.8f);
            double d2 = player.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
            double d3 = player.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * player.getBbWidth() * 0.8f);
            mutableBlockPos.set(d, d2, d3);
            BlockState blockState = player.level.getBlockState(mutableBlockPos);
            if (blockState.getRenderShape() == RenderShape.INVISIBLE || !blockState.isViewBlocking(player.level, mutableBlockPos)) continue;
            return blockState;
        }
        return null;
    }

    private static void renderTex(Minecraft minecraft, TextureAtlasSprite textureAtlasSprite, PoseStack poseStack) {
        minecraft.getTextureManager().bind(textureAtlasSprite.atlas().location());
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        float f = 0.1f;
        float f2 = -1.0f;
        float f3 = 1.0f;
        float f4 = -1.0f;
        float f5 = 1.0f;
        float f6 = -0.5f;
        float f7 = textureAtlasSprite.getU0();
        float f8 = textureAtlasSprite.getU1();
        float f9 = textureAtlasSprite.getV0();
        float f10 = textureAtlasSprite.getV1();
        Matrix4f matrix4f = poseStack.last().pose();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv(f8, f10).endVertex();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv(f7, f10).endVertex();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv(f7, f9).endVertex();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).uv(f8, f9).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
    }

    private static void renderWater(Minecraft minecraft, PoseStack poseStack) {
        RenderSystem.enableTexture();
        minecraft.getTextureManager().bind(UNDERWATER_LOCATION);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        float f = minecraft.player.getBrightness();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float f2 = 4.0f;
        float f3 = -1.0f;
        float f4 = 1.0f;
        float f5 = -1.0f;
        float f6 = 1.0f;
        float f7 = -0.5f;
        float f8 = -minecraft.player.yRot / 64.0f;
        float f9 = minecraft.player.xRot / 64.0f;
        Matrix4f matrix4f = poseStack.last().pose();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).color(f, f, f, 0.1f).uv(4.0f + f8, 4.0f + f9).endVertex();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).color(f, f, f, 0.1f).uv(0.0f + f8, 4.0f + f9).endVertex();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).color(f, f, f, 0.1f).uv(0.0f + f8, 0.0f + f9).endVertex();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).color(f, f, f, 0.1f).uv(4.0f + f8, 0.0f + f9).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.disableBlend();
    }

    private static void renderFire(Minecraft minecraft, PoseStack poseStack) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        TextureAtlasSprite textureAtlasSprite = ModelBakery.FIRE_1.sprite();
        minecraft.getTextureManager().bind(textureAtlasSprite.atlas().location());
        float f = textureAtlasSprite.getU0();
        float f2 = textureAtlasSprite.getU1();
        float f3 = (f + f2) / 2.0f;
        float f4 = textureAtlasSprite.getV0();
        float f5 = textureAtlasSprite.getV1();
        float f6 = (f4 + f5) / 2.0f;
        float f7 = textureAtlasSprite.uvShrinkRatio();
        float f8 = Mth.lerp(f7, f, f3);
        float f9 = Mth.lerp(f7, f2, f3);
        float f10 = Mth.lerp(f7, f4, f6);
        float f11 = Mth.lerp(f7, f5, f6);
        float f12 = 1.0f;
        for (int i = 0; i < 2; ++i) {
            poseStack.pushPose();
            float f13 = -0.5f;
            float f14 = 0.5f;
            float f15 = -0.5f;
            float f16 = 0.5f;
            float f17 = -0.5f;
            poseStack.translate((float)(-(i * 2 - 1)) * 0.24f, -0.30000001192092896, 0.0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float)(i * 2 - 1) * 10.0f));
            Matrix4f matrix4f = poseStack.last().pose();
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferBuilder.vertex(matrix4f, -0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv(f9, f11).endVertex();
            bufferBuilder.vertex(matrix4f, 0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv(f8, f11).endVertex();
            bufferBuilder.vertex(matrix4f, 0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv(f8, f10).endVertex();
            bufferBuilder.vertex(matrix4f, -0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).uv(f9, f10).endVertex();
            bufferBuilder.end();
            BufferUploader.end(bufferBuilder);
            poseStack.popPose();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}

