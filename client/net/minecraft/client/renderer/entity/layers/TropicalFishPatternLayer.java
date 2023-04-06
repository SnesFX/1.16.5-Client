/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ColorableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer
extends RenderLayer<TropicalFish, EntityModel<TropicalFish>> {
    private final TropicalFishModelA<TropicalFish> modelA = new TropicalFishModelA(0.008f);
    private final TropicalFishModelB<TropicalFish> modelB = new TropicalFishModelB(0.008f);

    public TropicalFishPatternLayer(RenderLayerParent<TropicalFish, EntityModel<TropicalFish>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, TropicalFish tropicalFish, float f, float f2, float f3, float f4, float f5, float f6) {
        ColorableListModel colorableListModel = tropicalFish.getBaseVariant() == 0 ? this.modelA : this.modelB;
        float[] arrf = tropicalFish.getPatternColor();
        TropicalFishPatternLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), colorableListModel, tropicalFish.getPatternTextureLocation(), poseStack, multiBufferSource, n, tropicalFish, f, f2, f4, f5, f6, f3, arrf[0], arrf[1], arrf[2]);
    }
}

