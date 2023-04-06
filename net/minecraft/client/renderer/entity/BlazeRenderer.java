/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeRenderer
extends MobRenderer<Blaze, BlazeModel<Blaze>> {
    private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

    public BlazeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BlazeModel(), 0.5f);
    }

    @Override
    protected int getBlockLightLevel(Blaze blaze, BlockPos blockPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(Blaze blaze) {
        return BLAZE_LOCATION;
    }
}

