/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Squid;

public class SquidRenderer
extends MobRenderer<Squid, SquidModel<Squid>> {
    private static final ResourceLocation SQUID_LOCATION = new ResourceLocation("textures/entity/squid.png");

    public SquidRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SquidModel(), 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(Squid squid) {
        return SQUID_LOCATION;
    }

    @Override
    protected void setupRotations(Squid squid, PoseStack poseStack, float f, float f2, float f3) {
        float f4 = Mth.lerp(f3, squid.xBodyRotO, squid.xBodyRot);
        float f5 = Mth.lerp(f3, squid.zBodyRotO, squid.zBodyRot);
        poseStack.translate(0.0, 0.5, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f2));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f4));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f5));
        poseStack.translate(0.0, -1.2000000476837158, 0.0);
    }

    @Override
    protected float getBob(Squid squid, float f) {
        return Mth.lerp(f, squid.oldTentacleAngle, squid.tentacleAngle);
    }
}

