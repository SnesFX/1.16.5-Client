/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;

public class FoxRenderer
extends MobRenderer<Fox, FoxModel<Fox>> {
    private static final ResourceLocation RED_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/fox.png");
    private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/fox_sleep.png");
    private static final ResourceLocation SNOW_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox.png");
    private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");

    public FoxRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new FoxModel(), 0.4f);
        this.addLayer(new FoxHeldItemLayer(this));
    }

    @Override
    protected void setupRotations(Fox fox, PoseStack poseStack, float f, float f2, float f3) {
        super.setupRotations(fox, poseStack, f, f2, f3);
        if (fox.isPouncing() || fox.isFaceplanted()) {
            float f4 = -Mth.lerp(f3, fox.xRotO, fox.xRot);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(f4));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Fox fox) {
        if (fox.getFoxType() == Fox.Type.RED) {
            return fox.isSleeping() ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
        }
        return fox.isSleeping() ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
    }
}

