/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderer
extends MobRenderer<Salmon, SalmonModel<Salmon>> {
    private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

    public SalmonRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SalmonModel(), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(Salmon salmon) {
        return SALMON_LOCATION;
    }

    @Override
    protected void setupRotations(Salmon salmon, PoseStack poseStack, float f, float f2, float f3) {
        super.setupRotations(salmon, poseStack, f, f2, f3);
        float f4 = 1.0f;
        float f5 = 1.0f;
        if (!salmon.isInWater()) {
            f4 = 1.3f;
            f5 = 1.7f;
        }
        float f6 = f4 * 4.3f * Mth.sin(f5 * 0.6f * f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f6));
        poseStack.translate(0.0, 0.0, -0.4000000059604645);
        if (!salmon.isInWater()) {
            poseStack.translate(0.20000000298023224, 0.10000000149011612, 0.0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f));
        }
    }
}

