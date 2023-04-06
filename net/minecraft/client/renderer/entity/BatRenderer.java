/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer
extends MobRenderer<Bat, BatModel> {
    private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

    public BatRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BatModel(), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(Bat bat) {
        return BAT_LOCATION;
    }

    @Override
    protected void scale(Bat bat, PoseStack poseStack, float f) {
        poseStack.scale(0.35f, 0.35f, 0.35f);
    }

    @Override
    protected void setupRotations(Bat bat, PoseStack poseStack, float f, float f2, float f3) {
        if (bat.isResting()) {
            poseStack.translate(0.0, -0.10000000149011612, 0.0);
        } else {
            poseStack.translate(0.0, Mth.cos(f * 0.3f) * 0.1f, 0.0);
        }
        super.setupRotations(bat, poseStack, f, f2, f3);
    }
}

