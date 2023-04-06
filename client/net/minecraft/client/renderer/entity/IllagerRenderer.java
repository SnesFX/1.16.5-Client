/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;

public abstract class IllagerRenderer<T extends AbstractIllager>
extends MobRenderer<T, IllagerModel<T>> {
    protected IllagerRenderer(EntityRenderDispatcher entityRenderDispatcher, IllagerModel<T> illagerModel, float f) {
        super(entityRenderDispatcher, illagerModel, f);
        this.addLayer(new CustomHeadLayer(this));
    }

    @Override
    protected void scale(T t, PoseStack poseStack, float f) {
        float f2 = 0.9375f;
        poseStack.scale(0.9375f, 0.9375f, 0.9375f);
    }
}

