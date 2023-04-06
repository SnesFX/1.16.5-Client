/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TntRenderer
extends EntityRenderer<PrimedTnt> {
    public TntRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(PrimedTnt primedTnt, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);
        if ((float)primedTnt.getLife() - f2 + 1.0f < 10.0f) {
            float f3 = 1.0f - ((float)primedTnt.getLife() - f2 + 1.0f) / 10.0f;
            f3 = Mth.clamp(f3, 0.0f, 1.0f);
            f3 *= f3;
            f3 *= f3;
            float f4 = 1.0f + f3 * 0.3f;
            poseStack.scale(f4, f4, f4);
        }
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0f));
        poseStack.translate(-0.5, -0.5, 0.5);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0f));
        TntMinecartRenderer.renderWhiteSolidBlock(Blocks.TNT.defaultBlockState(), poseStack, multiBufferSource, n, primedTnt.getLife() / 5 % 2 == 0);
        poseStack.popPose();
        super.render(primedTnt, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(PrimedTnt primedTnt) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

