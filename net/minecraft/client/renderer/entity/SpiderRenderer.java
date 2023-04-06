/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;

public class SpiderRenderer<T extends Spider>
extends MobRenderer<T, SpiderModel<T>> {
    private static final ResourceLocation SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/spider.png");

    public SpiderRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SpiderModel(), 0.8f);
        this.addLayer(new SpiderEyesLayer(this));
    }

    @Override
    protected float getFlipDegrees(T t) {
        return 180.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return SPIDER_LOCATION;
    }

    @Override
    protected /* synthetic */ float getFlipDegrees(LivingEntity livingEntity) {
        return this.getFlipDegrees((T)((Spider)livingEntity));
    }
}

