/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRenderer
extends MobRenderer<Chicken, ChickenModel<Chicken>> {
    private static final ResourceLocation CHICKEN_LOCATION = new ResourceLocation("textures/entity/chicken.png");

    public ChickenRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ChickenModel(), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Chicken chicken) {
        return CHICKEN_LOCATION;
    }

    @Override
    protected float getBob(Chicken chicken, float f) {
        float f2 = Mth.lerp(f, chicken.oFlap, chicken.flap);
        float f3 = Mth.lerp(f, chicken.oFlapSpeed, chicken.flapSpeed);
        return (Mth.sin(f2) + 1.0f) * f3;
    }
}

