/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ColorableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderer
extends MobRenderer<TropicalFish, EntityModel<TropicalFish>> {
    private final TropicalFishModelA<TropicalFish> modelA = new TropicalFishModelA(0.0f);
    private final TropicalFishModelB<TropicalFish> modelB = new TropicalFishModelB(0.0f);

    public TropicalFishRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new TropicalFishModelA(0.0f), 0.15f);
        this.addLayer(new TropicalFishPatternLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(TropicalFish tropicalFish) {
        return tropicalFish.getBaseTextureLocation();
    }

    @Override
    public void render(TropicalFish tropicalFish, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        ColorableListModel colorableListModel;
        this.model = colorableListModel = tropicalFish.getBaseVariant() == 0 ? this.modelA : this.modelB;
        float[] arrf = tropicalFish.getBaseColor();
        colorableListModel.setColor(arrf[0], arrf[1], arrf[2]);
        super.render(tropicalFish, f, f2, poseStack, multiBufferSource, n);
        colorableListModel.setColor(1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void setupRotations(TropicalFish tropicalFish, PoseStack poseStack, float f, float f2, float f3) {
        super.setupRotations(tropicalFish, poseStack, f, f2, f3);
        float f4 = 4.3f * Mth.sin(0.6f * f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f4));
        if (!tropicalFish.isInWater()) {
            poseStack.translate(0.20000000298023224, 0.10000000149011612, 0.0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f));
        }
    }
}

