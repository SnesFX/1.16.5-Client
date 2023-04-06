/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class ArrowLayer<T extends LivingEntity, M extends PlayerModel<T>>
extends StuckInBodyLayer<T, M> {
    private final EntityRenderDispatcher dispatcher;
    private Arrow arrow;

    public ArrowLayer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
        this.dispatcher = livingEntityRenderer.getDispatcher();
    }

    @Override
    protected int numStuck(T t) {
        return ((LivingEntity)t).getArrowCount();
    }

    @Override
    protected void renderStuckItem(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Entity entity, float f, float f2, float f3, float f4) {
        float f5 = Mth.sqrt(f * f + f3 * f3);
        this.arrow = new Arrow(entity.level, entity.getX(), entity.getY(), entity.getZ());
        this.arrow.yRot = (float)(Math.atan2(f, f3) * 57.2957763671875);
        this.arrow.xRot = (float)(Math.atan2(f2, f5) * 57.2957763671875);
        this.arrow.yRotO = this.arrow.yRot;
        this.arrow.xRotO = this.arrow.xRot;
        this.dispatcher.render(this.arrow, 0.0, 0.0, 0.0, 0.0f, f4, poseStack, multiBufferSource, n);
    }
}

