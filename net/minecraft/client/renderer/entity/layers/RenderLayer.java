/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class RenderLayer<T extends Entity, M extends EntityModel<T>> {
    private final RenderLayerParent<T, M> renderer;

    public RenderLayer(RenderLayerParent<T, M> renderLayerParent) {
        this.renderer = renderLayerParent;
    }

    protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRender(EntityModel<T> entityModel, EntityModel<T> entityModel2, ResourceLocation resourceLocation, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        if (!((Entity)t).isInvisible()) {
            entityModel.copyPropertiesTo(entityModel2);
            entityModel2.prepareMobModel(t, f, f2, f6);
            entityModel2.setupAnim(t, f, f2, f3, f4, f5);
            RenderLayer.renderColoredCutoutModel(entityModel2, resourceLocation, poseStack, multiBufferSource, n, t, f7, f8, f9);
        }
    }

    protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> entityModel, ResourceLocation resourceLocation, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3) {
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(resourceLocation));
        entityModel.renderToBuffer(poseStack, vertexConsumer, n, LivingEntityRenderer.getOverlayCoords(t, 0.0f), f, f2, f3, 1.0f);
    }

    public M getParentModel() {
        return this.renderer.getModel();
    }

    protected ResourceLocation getTextureLocation(T t) {
        return this.renderer.getTextureLocation(t);
    }

    public abstract void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10);
}

