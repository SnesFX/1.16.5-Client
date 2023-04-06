/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.SnowGolem;

public class SnowGolemRenderer
extends MobRenderer<SnowGolem, SnowGolemModel<SnowGolem>> {
    private static final ResourceLocation SNOW_GOLEM_LOCATION = new ResourceLocation("textures/entity/snow_golem.png");

    public SnowGolemRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SnowGolemModel(), 0.5f);
        this.addLayer(new SnowGolemHeadLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SnowGolem snowGolem) {
        return SNOW_GOLEM_LOCATION;
    }
}

