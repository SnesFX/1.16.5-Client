/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class StuckInBodyLayer<T extends LivingEntity, M extends PlayerModel<T>>
extends RenderLayer<T, M> {
    public StuckInBodyLayer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    protected abstract int numStuck(T var1);

    protected abstract void renderStuckItem(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        int n2 = this.numStuck(t);
        Random random = new Random(((Entity)t).getId());
        if (n2 <= 0) {
            return;
        }
        for (int i = 0; i < n2; ++i) {
            poseStack.pushPose();
            ModelPart modelPart = ((PlayerModel)this.getParentModel()).getRandomModelPart(random);
            ModelPart.Cube cube = modelPart.getRandomCube(random);
            modelPart.translateAndRotate(poseStack);
            float f7 = random.nextFloat();
            float f8 = random.nextFloat();
            float f9 = random.nextFloat();
            float f10 = Mth.lerp(f7, cube.minX, cube.maxX) / 16.0f;
            float f11 = Mth.lerp(f8, cube.minY, cube.maxY) / 16.0f;
            float f12 = Mth.lerp(f9, cube.minZ, cube.maxZ) / 16.0f;
            poseStack.translate(f10, f11, f12);
            f7 = -1.0f * (f7 * 2.0f - 1.0f);
            f8 = -1.0f * (f8 * 2.0f - 1.0f);
            f9 = -1.0f * (f9 * 2.0f - 1.0f);
            this.renderStuckItem(poseStack, multiBufferSource, n, (Entity)t, f7, f8, f9, f3);
            poseStack.popPose();
        }
    }
}

