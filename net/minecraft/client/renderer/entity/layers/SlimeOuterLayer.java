/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SlimeOuterLayer<T extends LivingEntity>
extends RenderLayer<T, SlimeModel<T>> {
    private final EntityModel<T> model = new SlimeModel(0);

    public SlimeOuterLayer(RenderLayerParent<T, SlimeModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        if (((Entity)t).isInvisible()) {
            return;
        }
        ((SlimeModel)this.getParentModel()).copyPropertiesTo(this.model);
        this.model.prepareMobModel(t, f, f2, f3);
        this.model.setupAnim(t, f, f2, f4, f5, f6);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(t)));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, LivingEntityRenderer.getOverlayCoords(t, 0.0f), 1.0f, 1.0f, 1.0f, 1.0f);
    }
}

