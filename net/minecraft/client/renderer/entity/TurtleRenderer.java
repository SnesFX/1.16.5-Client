/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleRenderer
extends MobRenderer<Turtle, TurtleModel<Turtle>> {
    private static final ResourceLocation TURTLE_LOCATION = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

    public TurtleRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new TurtleModel(0.0f), 0.7f);
    }

    @Override
    public void render(Turtle turtle, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (turtle.isBaby()) {
            this.shadowRadius *= 0.5f;
        }
        super.render(turtle, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(Turtle turtle) {
        return TURTLE_LOCATION;
    }
}

