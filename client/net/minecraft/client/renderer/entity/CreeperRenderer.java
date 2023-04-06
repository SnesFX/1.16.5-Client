/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRenderer
extends MobRenderer<Creeper, CreeperModel<Creeper>> {
    private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");

    public CreeperRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CreeperModel(), 0.5f);
        this.addLayer(new CreeperPowerLayer(this));
    }

    @Override
    protected void scale(Creeper creeper, PoseStack poseStack, float f) {
        float f2 = creeper.getSwelling(f);
        float f3 = 1.0f + Mth.sin(f2 * 100.0f) * f2 * 0.01f;
        f2 = Mth.clamp(f2, 0.0f, 1.0f);
        f2 *= f2;
        f2 *= f2;
        float f4 = (1.0f + f2 * 0.4f) * f3;
        float f5 = (1.0f + f2 * 0.1f) / f3;
        poseStack.scale(f4, f5, f4);
    }

    @Override
    protected float getWhiteOverlayProgress(Creeper creeper, float f) {
        float f2 = creeper.getSwelling(f);
        if ((int)(f2 * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return Mth.clamp(f2, 0.5f, 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(Creeper creeper) {
        return CREEPER_LOCATION;
    }

    @Override
    protected /* synthetic */ float getWhiteOverlayProgress(LivingEntity livingEntity, float f) {
        return this.getWhiteOverlayProgress((Creeper)livingEntity, f);
    }
}

