/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class SkeletonRenderer
extends HumanoidMobRenderer<AbstractSkeleton, SkeletonModel<AbstractSkeleton>> {
    private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    public SkeletonRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SkeletonModel(), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new SkeletonModel(0.5f, true), new SkeletonModel(1.0f, true)));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractSkeleton abstractSkeleton) {
        return SKELETON_LOCATION;
    }
}

