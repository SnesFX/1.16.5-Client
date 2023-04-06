/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class HumanoidMobRenderer<T extends Mob, M extends HumanoidModel<T>>
extends MobRenderer<T, M> {
    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation("textures/entity/steve.png");

    public HumanoidMobRenderer(EntityRenderDispatcher entityRenderDispatcher, M m, float f) {
        this(entityRenderDispatcher, m, f, 1.0f, 1.0f, 1.0f);
    }

    public HumanoidMobRenderer(EntityRenderDispatcher entityRenderDispatcher, M m, float f, float f2, float f3, float f4) {
        super(entityRenderDispatcher, m, f);
        this.addLayer(new CustomHeadLayer(this, f2, f3, f4));
        this.addLayer(new ElytraLayer(this));
        this.addLayer(new ItemInHandLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return DEFAULT_LOCATION;
    }
}

