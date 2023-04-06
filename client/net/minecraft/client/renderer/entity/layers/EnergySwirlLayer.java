/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PowerableMob;

public abstract class EnergySwirlLayer<T extends Entity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    public EnergySwirlLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!((PowerableMob)t).isPowered()) {
            return;
        }
        float f7 = (float)((Entity)t).tickCount + f3;
        EntityModel<T> entityModel = this.model();
        entityModel.prepareMobModel(t, f, f2, f3);
        ((EntityModel)this.getParentModel()).copyPropertiesTo(entityModel);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset(f7), f7 * 0.01f));
        entityModel.setupAnim(t, f, f2, f4, f5, f6);
        entityModel.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 0.5f, 0.5f, 0.5f, 1.0f);
    }

    protected abstract float xOffset(float var1);

    protected abstract ResourceLocation getTextureLocation();

    protected abstract EntityModel<T> model();
}

