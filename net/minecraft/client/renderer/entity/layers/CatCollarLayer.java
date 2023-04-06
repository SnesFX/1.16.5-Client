/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.DyeColor;

public class CatCollarLayer
extends RenderLayer<Cat, CatModel<Cat>> {
    private static final ResourceLocation CAT_COLLAR_LOCATION = new ResourceLocation("textures/entity/cat/cat_collar.png");
    private final CatModel<Cat> catModel = new CatModel(0.01f);

    public CatCollarLayer(RenderLayerParent<Cat, CatModel<Cat>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Cat cat, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!cat.isTame()) {
            return;
        }
        float[] arrf = cat.getCollarColor().getTextureDiffuseColors();
        CatCollarLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.catModel, CAT_COLLAR_LOCATION, poseStack, multiBufferSource, n, cat, f, f2, f4, f5, f6, f3, arrf[0], arrf[1], arrf[2]);
    }
}

