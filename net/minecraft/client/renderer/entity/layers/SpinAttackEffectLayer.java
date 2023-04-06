/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SpinAttackEffectLayer<T extends LivingEntity>
extends RenderLayer<T, PlayerModel<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident_riptide.png");
    private final ModelPart box = new ModelPart(64, 64, 0, 0);

    public SpinAttackEffectLayer(RenderLayerParent<T, PlayerModel<T>> renderLayerParent) {
        super(renderLayerParent);
        this.box.addBox(-8.0f, -16.0f, -8.0f, 16.0f, 32.0f, 16.0f);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!((LivingEntity)t).isAutoSpinAttack()) {
            return;
        }
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        for (int i = 0; i < 3; ++i) {
            poseStack.pushPose();
            float f7 = f4 * (float)(-(45 + i * 5));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f7));
            float f8 = 0.75f * (float)i;
            poseStack.scale(f8, f8, f8);
            poseStack.translate(0.0, -0.2f + 0.6f * (float)i, 0.0);
            this.box.render(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}

