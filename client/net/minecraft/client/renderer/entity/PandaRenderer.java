/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Panda;

public class PandaRenderer
extends MobRenderer<Panda, PandaModel<Panda>> {
    private static final Map<Panda.Gene, ResourceLocation> TEXTURES = Util.make(Maps.newEnumMap(Panda.Gene.class), enumMap -> {
        enumMap.put(Panda.Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
        enumMap.put(Panda.Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
        enumMap.put(Panda.Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
        enumMap.put(Panda.Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
        enumMap.put(Panda.Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
        enumMap.put(Panda.Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
        enumMap.put(Panda.Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
    });

    public PandaRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PandaModel(9, 0.0f), 0.9f);
        this.addLayer(new PandaHoldsItemLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Panda panda) {
        return TEXTURES.getOrDefault((Object)panda.getVariant(), TEXTURES.get((Object)Panda.Gene.NORMAL));
    }

    @Override
    protected void setupRotations(Panda panda, PoseStack poseStack, float f, float f2, float f3) {
        float f4;
        float f5;
        float f6;
        super.setupRotations(panda, poseStack, f, f2, f3);
        if (panda.rollCounter > 0) {
            float f7;
            int n = panda.rollCounter;
            int n2 = n + 1;
            f6 = 7.0f;
            float f8 = f7 = panda.isBaby() ? 0.3f : 0.8f;
            if (n < 8) {
                float f9 = (float)(90 * n) / 7.0f;
                float f10 = (float)(90 * n2) / 7.0f;
                float f11 = this.getAngle(f9, f10, n2, f3, 8.0f);
                poseStack.translate(0.0, (f7 + 0.2f) * (f11 / 90.0f), 0.0);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-f11));
            } else if (n < 16) {
                float f12 = ((float)n - 8.0f) / 7.0f;
                float f13 = 90.0f + 90.0f * f12;
                float f14 = 90.0f + 90.0f * ((float)n2 - 8.0f) / 7.0f;
                float f15 = this.getAngle(f13, f14, n2, f3, 16.0f);
                poseStack.translate(0.0, f7 + 0.2f + (f7 - 0.2f) * (f15 - 90.0f) / 90.0f, 0.0);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-f15));
            } else if ((float)n < 24.0f) {
                float f16 = ((float)n - 16.0f) / 7.0f;
                float f17 = 180.0f + 90.0f * f16;
                float f18 = 180.0f + 90.0f * ((float)n2 - 16.0f) / 7.0f;
                float f19 = this.getAngle(f17, f18, n2, f3, 24.0f);
                poseStack.translate(0.0, f7 + f7 * (270.0f - f19) / 90.0f, 0.0);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-f19));
            } else if (n < 32) {
                float f20 = ((float)n - 24.0f) / 7.0f;
                float f21 = 270.0f + 90.0f * f20;
                float f22 = 270.0f + 90.0f * ((float)n2 - 24.0f) / 7.0f;
                float f23 = this.getAngle(f21, f22, n2, f3, 32.0f);
                poseStack.translate(0.0, f7 * ((360.0f - f23) / 90.0f), 0.0);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-f23));
            }
        }
        if ((f4 = panda.getSitAmount(f3)) > 0.0f) {
            poseStack.translate(0.0, 0.8f * f4, 0.0);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f4, panda.xRot, panda.xRot + 90.0f)));
            poseStack.translate(0.0, -1.0f * f4, 0.0);
            if (panda.isScared()) {
                float f24 = (float)(Math.cos((double)panda.tickCount * 1.25) * 3.141592653589793 * 0.05000000074505806);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(f24));
                if (panda.isBaby()) {
                    poseStack.translate(0.0, 0.800000011920929, 0.550000011920929);
                }
            }
        }
        if ((f5 = panda.getLieOnBackAmount(f3)) > 0.0f) {
            f6 = panda.isBaby() ? 0.5f : 1.3f;
            poseStack.translate(0.0, f6 * f5, 0.0);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f5, panda.xRot, panda.xRot + 180.0f)));
        }
    }

    private float getAngle(float f, float f2, int n, float f3, float f4) {
        if ((float)n < f4) {
            return Mth.lerp(f3, f, f2);
        }
        return f;
    }
}

