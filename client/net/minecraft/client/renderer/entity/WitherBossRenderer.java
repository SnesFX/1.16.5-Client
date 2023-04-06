/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossRenderer
extends MobRenderer<WitherBoss, WitherBossModel<WitherBoss>> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");

    public WitherBossRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WitherBossModel(0.0f), 1.0f);
        this.addLayer(new WitherArmorLayer(this));
    }

    @Override
    protected int getBlockLightLevel(WitherBoss witherBoss, BlockPos blockPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(WitherBoss witherBoss) {
        int n = witherBoss.getInvulnerableTicks();
        if (n <= 0 || n <= 80 && n / 5 % 2 == 1) {
            return WITHER_LOCATION;
        }
        return WITHER_INVULNERABLE_LOCATION;
    }

    @Override
    protected void scale(WitherBoss witherBoss, PoseStack poseStack, float f) {
        float f2 = 2.0f;
        int n = witherBoss.getInvulnerableTicks();
        if (n > 0) {
            f2 -= ((float)n - f) / 220.0f * 0.5f;
        }
        poseStack.scale(f2, f2, f2);
    }
}

