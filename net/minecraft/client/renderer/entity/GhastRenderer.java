/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;

public class GhastRenderer
extends MobRenderer<Ghast, GhastModel<Ghast>> {
    private static final ResourceLocation GHAST_LOCATION = new ResourceLocation("textures/entity/ghast/ghast.png");
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

    public GhastRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new GhastModel(), 1.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Ghast ghast) {
        if (ghast.isCharging()) {
            return GHAST_SHOOTING_LOCATION;
        }
        return GHAST_LOCATION;
    }

    @Override
    protected void scale(Ghast ghast, PoseStack poseStack, float f) {
        float f2 = 1.0f;
        float f3 = 4.5f;
        float f4 = 4.5f;
        poseStack.scale(4.5f, 4.5f, 4.5f);
    }
}

