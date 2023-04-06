/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Ocelot;

public class OcelotRenderer
extends MobRenderer<Ocelot, OcelotModel<Ocelot>> {
    private static final ResourceLocation CAT_OCELOT_LOCATION = new ResourceLocation("textures/entity/cat/ocelot.png");

    public OcelotRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new OcelotModel(0.0f), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(Ocelot ocelot) {
        return CAT_OCELOT_LOCATION;
    }
}

