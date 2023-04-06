/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;

public class SlimeRenderer
extends MobRenderer<Slime, SlimeModel<Slime>> {
    private static final ResourceLocation SLIME_LOCATION = new ResourceLocation("textures/entity/slime/slime.png");

    public SlimeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SlimeModel(16), 0.25f);
        this.addLayer(new SlimeOuterLayer<Slime>(this));
    }

    @Override
    public void render(Slime slime, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        this.shadowRadius = 0.25f * (float)slime.getSize();
        super.render(slime, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    protected void scale(Slime slime, PoseStack poseStack, float f) {
        float f2 = 0.999f;
        poseStack.scale(0.999f, 0.999f, 0.999f);
        poseStack.translate(0.0, 0.0010000000474974513, 0.0);
        float f3 = slime.getSize();
        float f4 = Mth.lerp(f, slime.oSquish, slime.squish) / (f3 * 0.5f + 1.0f);
        float f5 = 1.0f / (f4 + 1.0f);
        poseStack.scale(f5 * f3, 1.0f / f5 * f3, f5 * f3);
    }

    @Override
    public ResourceLocation getTextureLocation(Slime slime) {
        return SLIME_LOCATION;
    }
}

