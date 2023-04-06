/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public abstract class AbstractHorseRenderer<T extends AbstractHorse, M extends HorseModel<T>>
extends MobRenderer<T, M> {
    private final float scale;

    public AbstractHorseRenderer(EntityRenderDispatcher entityRenderDispatcher, M m, float f) {
        super(entityRenderDispatcher, m, 0.75f);
        this.scale = f;
    }

    @Override
    protected void scale(T t, PoseStack poseStack, float f) {
        poseStack.scale(this.scale, this.scale, this.scale);
        super.scale(t, poseStack, f);
    }
}

