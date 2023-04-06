/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

public class ArmorStandRenderer
extends LivingEntityRenderer<ArmorStand, ArmorStandArmorModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = new ResourceLocation("textures/entity/armorstand/wood.png");

    public ArmorStandRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ArmorStandModel(), 0.0f);
        this.addLayer(new HumanoidArmorLayer<ArmorStand, ArmorStandArmorModel, ArmorStandArmorModel>(this, new ArmorStandArmorModel(0.5f), new ArmorStandArmorModel(1.0f)));
        this.addLayer(new ItemInHandLayer<ArmorStand, ArmorStandArmorModel>(this));
        this.addLayer(new ElytraLayer<ArmorStand, ArmorStandArmorModel>(this));
        this.addLayer(new CustomHeadLayer<ArmorStand, ArmorStandArmorModel>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorStand armorStand) {
        return DEFAULT_SKIN_LOCATION;
    }

    @Override
    protected void setupRotations(ArmorStand armorStand, PoseStack poseStack, float f, float f2, float f3) {
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f2));
        float f4 = (float)(armorStand.level.getGameTime() - armorStand.lastHit) + f3;
        if (f4 < 5.0f) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f4 / 1.5f * 3.1415927f) * 3.0f));
        }
    }

    @Override
    protected boolean shouldShowName(ArmorStand armorStand) {
        float f;
        double d = this.entityRenderDispatcher.distanceToSqr(armorStand);
        float f2 = f = armorStand.isCrouching() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        return armorStand.isCustomNameVisible();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(ArmorStand armorStand, boolean bl, boolean bl2, boolean bl3) {
        if (!armorStand.isMarker()) {
            return super.getRenderType(armorStand, bl, bl2, bl3);
        }
        ResourceLocation resourceLocation = this.getTextureLocation(armorStand);
        if (bl2) {
            return RenderType.entityTranslucent(resourceLocation, false);
        }
        if (bl) {
            return RenderType.entityCutoutNoCull(resourceLocation, false);
        }
        return null;
    }
}

