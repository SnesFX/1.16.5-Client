/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.LlamaSpit;

public class LlamaSpitRenderer
extends EntityRenderer<LlamaSpit> {
    private static final ResourceLocation LLAMA_SPIT_LOCATION = new ResourceLocation("textures/entity/llama/spit.png");
    private final LlamaSpitModel<LlamaSpit> model = new LlamaSpitModel();

    public LlamaSpitRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(LlamaSpit llamaSpit, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.15000000596046448, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(f2, llamaSpit.yRotO, llamaSpit.yRot) - 90.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(f2, llamaSpit.xRotO, llamaSpit.xRot)));
        this.model.setupAnim(llamaSpit, f2, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(LLAMA_SPIT_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        super.render(llamaSpit, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(LlamaSpit llamaSpit) {
        return LLAMA_SPIT_LOCATION;
    }
}

