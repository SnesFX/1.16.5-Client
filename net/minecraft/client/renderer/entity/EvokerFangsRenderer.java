/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EvokerFangs;

public class EvokerFangsRenderer
extends EntityRenderer<EvokerFangs> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsModel<EvokerFangs> model = new EvokerFangsModel();

    public EvokerFangsRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(EvokerFangs evokerFangs, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        float f3 = evokerFangs.getAnimationProgress(f2);
        if (f3 == 0.0f) {
            return;
        }
        float f4 = 2.0f;
        if (f3 > 0.9f) {
            f4 = (float)((double)f4 * ((1.0 - (double)f3) / 0.10000000149011612));
        }
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0f - evokerFangs.yRot));
        poseStack.scale(-f4, -f4, f4);
        float f5 = 0.03125f;
        poseStack.translate(0.0, -0.6259999871253967, 0.0);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        this.model.setupAnim(evokerFangs, f3, 0.0f, 0.0f, evokerFangs.yRot, evokerFangs.xRot);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        super.render(evokerFangs, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(EvokerFangs evokerFangs) {
        return TEXTURE_LOCATION;
    }
}

