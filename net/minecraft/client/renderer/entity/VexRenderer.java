/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;

public class VexRenderer
extends HumanoidMobRenderer<Vex, VexModel> {
    private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
    private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

    public VexRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new VexModel(), 0.3f);
    }

    @Override
    protected int getBlockLightLevel(Vex vex, BlockPos blockPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(Vex vex) {
        if (vex.isCharging()) {
            return VEX_CHARGING_LOCATION;
        }
        return VEX_LOCATION;
    }

    @Override
    protected void scale(Vex vex, PoseStack poseStack, float f) {
        poseStack.scale(0.4f, 0.4f, 0.4f);
    }
}

