/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;

public class MagmaCubeRenderer
extends MobRenderer<MagmaCube, LavaSlimeModel<MagmaCube>> {
    private static final ResourceLocation MAGMACUBE_LOCATION = new ResourceLocation("textures/entity/slime/magmacube.png");

    public MagmaCubeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new LavaSlimeModel(), 0.25f);
    }

    @Override
    protected int getBlockLightLevel(MagmaCube magmaCube, BlockPos blockPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(MagmaCube magmaCube) {
        return MAGMACUBE_LOCATION;
    }

    @Override
    protected void scale(MagmaCube magmaCube, PoseStack poseStack, float f) {
        int n = magmaCube.getSize();
        float f2 = Mth.lerp(f, magmaCube.oSquish, magmaCube.squish) / ((float)n * 0.5f + 1.0f);
        float f3 = 1.0f / (f2 + 1.0f);
        poseStack.scale(f3 * (float)n, 1.0f / f3 * (float)n, f3 * (float)n);
    }
}

