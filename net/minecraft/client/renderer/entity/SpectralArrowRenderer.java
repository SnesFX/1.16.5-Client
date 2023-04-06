/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.SpectralArrow;

public class SpectralArrowRenderer
extends ArrowRenderer<SpectralArrow> {
    public static final ResourceLocation SPECTRAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

    public SpectralArrowRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public ResourceLocation getTextureLocation(SpectralArrow spectralArrow) {
        return SPECTRAL_ARROW_LOCATION;
    }
}

