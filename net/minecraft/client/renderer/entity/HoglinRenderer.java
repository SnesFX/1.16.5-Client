/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class HoglinRenderer
extends MobRenderer<Hoglin, HoglinModel<Hoglin>> {
    private static final ResourceLocation HOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/hoglin.png");

    public HoglinRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HoglinModel(), 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(Hoglin hoglin) {
        return HOGLIN_LOCATION;
    }

    @Override
    protected boolean isShaking(Hoglin hoglin) {
        return hoglin.isConverting();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity livingEntity) {
        return this.isShaking((Hoglin)livingEntity);
    }
}

