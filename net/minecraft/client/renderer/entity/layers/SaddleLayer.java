/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;

public class SaddleLayer<T extends Entity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private final ResourceLocation textureLocation;
    private final M model;

    public SaddleLayer(RenderLayerParent<T, M> renderLayerParent, M m, ResourceLocation resourceLocation) {
        super(renderLayerParent);
        this.model = m;
        this.textureLocation = resourceLocation;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!((Saddleable)t).isSaddled()) {
            return;
        }
        ((EntityModel)this.getParentModel()).copyPropertiesTo(this.model);
        ((EntityModel)this.model).prepareMobModel(t, f, f2, f3);
        ((EntityModel)this.model).setupAnim(t, f, f2, f4, f5, f6);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));
        ((Model)this.model).renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}

