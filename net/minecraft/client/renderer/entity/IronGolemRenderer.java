/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemRenderer
extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {
    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

    public IronGolemRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IronGolemModel(), 0.7f);
        this.addLayer(new IronGolemCrackinessLayer(this));
        this.addLayer(new IronGolemFlowerLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(IronGolem ironGolem) {
        return GOLEM_LOCATION;
    }

    @Override
    protected void setupRotations(IronGolem ironGolem, PoseStack poseStack, float f, float f2, float f3) {
        super.setupRotations(ironGolem, poseStack, f, f2, f3);
        if ((double)ironGolem.animationSpeed < 0.01) {
            return;
        }
        float f4 = 13.0f;
        float f5 = ironGolem.animationPosition - ironGolem.animationSpeed * (1.0f - f3) + 6.0f;
        float f6 = (Math.abs(f5 % 13.0f - 6.5f) - 3.25f) / 3.25f;
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(6.5f * f6));
    }
}

